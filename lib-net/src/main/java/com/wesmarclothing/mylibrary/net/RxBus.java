package com.wesmarclothing.mylibrary.net;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Administrator on 2018/1/3.
 * 该类的作用就是为了替代eventBus
 * http://blog.csdn.net/zcphappy/article/details/75255918
 */

public class RxBus {
    private final Subject<Object> mBus;
    private static volatile RxBus instance;
    private final Map<Class<?>, Object> mStickyEventMap;

    /**
     * 默认私有化构造函数
     */
    private RxBus() {
        mBus = PublishSubject.create().toSerialized();
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    /**
     * 单例模式
     */
    public static RxBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    /**
     * 将数据添加到订阅
     * 这个地方是再添加订阅的地方。最好创建一个新的类用于数据的传递
     */
    public void post(@NonNull Object obj) {
        if (mBus.hasObservers()) {//判断当前是否已经添加订阅
            mBus.onNext(obj);
        }
    }

    /**
     * 这个是传递集合如果有需要的话你也可以进行更改
     */
    public void post(@NonNull List<Object> obj) {
        if (mBus.hasObservers()) {//判断当前是否已经添加订阅
            mBus.onNext(obj);
        }
    }

    /**
     * 发送一个新Sticky事件
     */
    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        mBus.onNext(event);
    }

    /**
     * 注册，传递tClass的时候最好创建一个封装的类。这对数据的传递作用
     * 新更改仅仅抛出生成类和解析,有会泄露的问题，
     */
    @Deprecated
    public <T> Disposable register(Class<T> tClass, Consumer<T> consumer) {
        Type type = new TypeToken<T>() {
        }.getType();
        return mBus.ofType(tClass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }


    /**
     * 注册，传递tClass的时候最好创建一个封装的类。这对数据的传递作用
     * 新更改仅仅抛出生成类和解析
     * 返回一个订阅者，然后可以绑定生命周期或者做一些其他的事情
     */
    public <T> Observable<T> register2(Class<T> tClass) {
        return mBus.ofType(tClass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发送粘性事件
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Observable<T> registerSticky(final Class<T> tClass) {
        synchronized (mStickyEventMap) {
            Observable<T> observable = mBus.ofType(tClass);
            final Object event = mStickyEventMap.get(tClass);

            if (observable == null) {
                throw new NullPointerException("Observable == null");
            }

            if (event != null) {
                return observable.mergeWith(Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                        emitter.onNext(tClass.cast(event));
                    }
                }));
            } else {
                return observable;
            }
        }
    }


    /**
     * 保存订阅后的disposable
     *
     * @param o
     * @param disposable
     */
    private HashMap<String, CompositeDisposable> mSubscriptionMap;

    public void addSubscription(Object o, Disposable... disposables) {
        if (mSubscriptionMap == null) {
            mSubscriptionMap = new HashMap<>();
        }
        String key = o.getClass().getName();

        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).addAll(disposables);
        } else {
            //一次性容器,可以持有多个并提供 添加和移除。
            CompositeDisposable mCompositeDisposable = new CompositeDisposable();
            mCompositeDisposable.addAll(disposables);
            mSubscriptionMap.put(key, mCompositeDisposable);
        }
    }

    /**
     * 取消订阅
     *
     * @param o 这个是你添加到订阅的的对象
     */
    public void unSubscribe(Object o) {
        if (mSubscriptionMap == null) {
            return;
        }

        String key = o.getClass().getName();
        if (!mSubscriptionMap.containsKey(key)) {
            return;
        }
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).dispose();
        }

        mSubscriptionMap.remove(key);
    }


    /**
     * 根据eventType获取Sticky事件
     */
    public <T> T getStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.get(eventType));
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }


}