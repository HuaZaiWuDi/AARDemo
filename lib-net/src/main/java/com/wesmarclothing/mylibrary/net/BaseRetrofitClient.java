package com.wesmarclothing.mylibrary.net;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Package com.wesmarclothing.mylibrary.net
 * @FileName BaseRetrofitClient
 * @Date 2019/6/13 15:35
 * @Author JACK
 * @Describe TODO
 * @Project WeiMiBra
 */
public abstract class BaseRetrofitClient {
    public static final String TAG = "【NetManager】";

    public OkHttpClient mClient;
    public OkHttpClient.Builder builder;
    public boolean showLog = true;

    public OkHttpClient getClient() {
        builder = new OkHttpClient.Builder();

        if (showLog) {
            //日志显示级别
            HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;

            StringBuilder mMessage = new StringBuilder();
            //新建log拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                if (message.startsWith("--> POST")) {
                    mMessage.setLength(0);
                }
                if ((message.startsWith("{") && message.endsWith("}"))
                        || (message.startsWith("[") && message.endsWith("]"))) {
                    message = Logger.formatJson(message);
                }
                mMessage.append(message.concat("\n"));
                if (message.startsWith("<-- END HTTP")) {
                    Logger.e(TAG, mMessage.toString());
                }
            });
            loggingInterceptor.setLevel(level);
            //OkHttp进行添加拦截器loggingInterceptor
            builder.addNetworkInterceptor(loggingInterceptor);
        }
        handleBuilder(builder);
        return mClient;
    }

    protected abstract void handleBuilder(OkHttpClient.Builder builder);


    public <S> S getService(Class<S> serviceClass, String baseUrl) {
        return new Retrofit.Builder()
                .client(mClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .baseUrl(baseUrl)
                .build()
                .create(serviceClass);
    }


    class CacheInterceptor implements Interceptor {
        private File httpCacheDirectory;
        private long cacheSize;
        private boolean hasNet;

        public CacheInterceptor(File httpCacheDirectory, long cacheSize, boolean hasNet) {
            this.httpCacheDirectory = httpCacheDirectory;
            this.cacheSize = cacheSize;
            this.hasNet = hasNet;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!hasNet) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (!hasNet) {
                long maxAge = 60 * 60;
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                long maxStale = 60 * 60 * 24 * 28;// tolerate 4-weeks stale
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }

}
