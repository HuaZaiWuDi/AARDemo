package com.wesmarclothing.mylibrary.net;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * 项目名称：BleCar
 * 类描述：
 * 创建人：oden
 * 创建时间：2016/8/30 11:27
 */
public abstract class RxSubscriber<T> implements Observer<T> {

    String TAG = "【RxSubscriber】";


    public RxSubscriber() {
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable e) {
        Logger.e(TAG, "onError: " + e.getMessage());
    }

    @Override
    public void onComplete() {
        Logger.i(TAG, "onComplete：");
    }

    @Override
    public void onNext(T t) {
        Logger.i(TAG, "onNext：" + t);
        _onNext(t);
    }


    protected abstract void _onNext(T t);
}
