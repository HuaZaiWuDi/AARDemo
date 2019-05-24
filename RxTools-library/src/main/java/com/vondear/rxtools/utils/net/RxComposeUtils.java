package com.vondear.rxtools.utils.net;

import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.text.TextUtils;

import com.kongzue.dialog.v2.WaitDialog;
import com.vondear.rxtools.model.lifecycyle.LifeCycleEvent;

import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * 项目名称：MyProject
 * 类描述：使用方法compose（），此操作符是操作整个流，给我一个observable，还给你一个同样的observable
 * 创建人：Jack
 * 创建时间：2018/4/17
 */
public class RxComposeUtils {


    /**
     * 统一线程处理
     *
     * @param <T> 指定的泛型类型
     * @return FlowableTransformer
     */
    public static <T> FlowableTransformer<T, T> rxThreaHelper() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> flowable) {
                return flowable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    /**
     * 统一线程处理
     *
     * @param <T> 指定的泛型类型
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> rxThreadHelper() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };


    }

    public static <T> ObservableTransformer<T, T> showDialog(final Context mContext) {
        return observable -> observable.doOnSubscribe(disposable ->
                WaitDialog.show(mContext, "正在加载"))
                .doFinally((Action) () -> {
                    WaitDialog.dismiss();
                });
    }


    /**
     * 统一线程处理
     *
     * @param <T> 指定的泛型类型
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> showDialog(final Dialog dialog) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                if (dialog != null)
                                    dialog.show();
                            }
                        }).doFinally(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (dialog != null)
                                    dialog.dismiss();
                            }
                        });
            }
        };
    }


    /**
     * 得到 Observable
     *
     * @param <T> 指定的泛型类型
     * @return Observable
     */
    private static <T> Observable<T> createData(final T t) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(t);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * 对结果进行预处理
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> handleResult() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T t) throws Exception {
                        if (t instanceof String) {
                            JSONObject object = null;
                            try {
                                object = new JSONObject((String) t);
                                int code = object.getInt("code");
                                String msg = object.getString("msg");
                                if (code == 0) {
                                    if (object.has("data")) {
                                        String data = object.getString("data");
                                        if (TextUtils.isEmpty(data)) {
                                            return Observable.error(new ExplainException(data, -1));
                                        }
                                        return createData((T) data);
                                    } else {
                                        return createData((T) "");
                                    }
                                } else {
                                    return Observable.error(new ExplainException(msg, code));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return Observable.error(new ExplainException("数据异常", -1));
                            }
                        }
                        return Observable.error(new ExplainException("数据异常", -1));
                    }
                });
            }
        };
    }


    /**
     * 对结果进行预处理
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<HttpResult<T>, T> handleResult2() {
        return new ObservableTransformer<HttpResult<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<HttpResult<T>> upstream) {
                return upstream.flatMap(new Function<HttpResult<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(HttpResult<T> t) throws Exception {
                        if (t.getCode() == 0) {
                            return createData(t.getData());
                        } else {
                            return Observable.error(new ExplainException(t.getMessage(), t.getCode()));
                        }
                    }
                });
            }
        };
    }


    /**
     * 绑定生命周期，在AC和Fragment在销毁时结束网络请求
     *
     * @param <T> 指定的泛型类型
     * @return Observable
     * <p>
     * takeUtil，很显然，observable.takeUtil(condition)，当condition == true时终止，且包含临界条件的item
     */
    public static <T> ObservableTransformer<T, T> bindLife(final BehaviorSubject<LifeCycleEvent> subject) {
        return upstream -> {
            if (subject == null) return upstream;
            return upstream.takeUntil(subject.skipWhile(activityLifeCycleEvent -> activityLifeCycleEvent != LifeCycleEvent.DESTROY && activityLifeCycleEvent != LifeCycleEvent.DETACH));
        };
    }

    /**
     * 绑定生命周期，在AC和Fragment在销毁时结束网络请求
     *
     * @param <T> 指定的泛型类型
     * @return Observable
     * <p>
     * takeUtil，很显然，observable.takeUtil(condition)，当condition == true时终止，且包含临界条件的item
     * </T>
     */
    public static <T> ObservableTransformer<T, T> bindLife(LifecycleOwner owner) {
        return upstream -> upstream.takeUntil(t -> owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED);
    }


    /**
     * 绑定生命周期，在AC和Fragment在显示后才加载
     *
     * @param <T> 指定的泛型类型
     * @return Observable
     * <p>
     * takeUtil，很显然，observable.takeUtil(condition)，当condition == true时终止，且包含临界条件的item
     */
    public static <T> ObservableTransformer<T, T> bindLifeResume(final BehaviorSubject<LifeCycleEvent> subject) {
        return upstream -> {
            if (subject == null) return upstream;
            return upstream.takeUntil(subject.skipWhile(activityLifeCycleEvent -> activityLifeCycleEvent != LifeCycleEvent.DESTROY && activityLifeCycleEvent != LifeCycleEvent.DETACH
                    && activityLifeCycleEvent != LifeCycleEvent.CREATE && activityLifeCycleEvent != LifeCycleEvent.ATTACH));
        };
    }


}
