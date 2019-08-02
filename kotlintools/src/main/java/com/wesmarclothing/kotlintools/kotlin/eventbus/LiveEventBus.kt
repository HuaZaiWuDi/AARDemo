package com.wesmarclothing.kotlintools.kotlin.eventbus

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.ExternalLiveData
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.gson.reflect.TypeToken
import com.wesmarclothing.kotlintools.kotlin.eventbus.LiveEventBus.ObserverWrapper
import com.wesmarclothing.kotlintools.kotlin.utils.isMainThread
import java.lang.reflect.Type
import java.util.*

/**
 * @Package com.wesmarclothing.kotlintools.kotlin.eventbus
 * @FileName LiveEventBus
 * @Date 2019/7/22 17:07
 * @Author JACK
 * @Describe TODO
 * @Project WeiMiBra
 *
 *
 *
 *   _     _           _____                _  ______
 *  | |   (_)         |  ___|              | | | ___ \
 *  | |    ___   _____| |____   _____ _ __ | |_| |_/ /_   _ ___
 *  | |   | \ \ / / _ \  __\ \ / / _ \ '_ \| __| ___ \ | | / __|
 *  | |___| |\ V /  __/ |___\ V /  __/ | | | |_| |_/ / |_| \__ \
 *  \_____/_| \_/ \___\____/ \_/ \___|_| |_|\__\____/ \__,_|___/
 *
 *
 */


object LiveEventBus {


    //    private val bus: MutableMap<Class<*>, LiveEvent<*>> = mutableMapOf()
    /**
     * 使用Kotlin类型进行引用类型的包装，
     * 因为基本数据类型中Int::class.java返回的是拆箱之后的int
     * post使用泛型之后，基本数据类型变成了Integer装箱类型
     */
    private val bus: MutableMap<Type, LifecycleLiveData<*>> = mutableMapOf()
    //    private val busMap: MutableMap<Type, LifecycleLiveData<*>> = mutableMapOf()
    private var lifecycleObserverAlwaysActive = true
    private var autoClear = false
    private val mainHandler = Handler(Looper.getMainLooper())


    @Synchronized
    fun <T : Any> fetch(): LifecycleLiveData<T> {

        val type = object : TypeToken<T>() {}.type

        if (!bus.containsKey(type)) {
            bus[type] = LifecycleLiveData<T>()
        }
//        bus.keys.d("FETCH")

        return bus[type] as LifecycleLiveData<T>
    }


    fun <T : Any> post(value: T) {
        val type = object : TypeToken<T>() {}.type

        if (!bus.containsKey(type)) {
            bus[type] = LifecycleLiveData<T>()
        }

//        bus.keys.d("POST")
        runMainThread {
            postInternal(value)
        }
    }

    fun postDelay(value: Any, delay: Long) {
        mainHandler.postDelayed({
            postInternal(value)
        }, delay)
    }

    @MainThread
    @Synchronized
    private fun <T : Any> postInternal(value: T) {
        val type = object : TypeToken<T>() {}.type
        val liveData = bus[type] as LifecycleLiveData<T>
        liveData.value = value
    }


    class LifecycleLiveData<T> : ExternalLiveData<T>() {

        private val observerMap = HashMap<Observer<in T>, LiveEventBus.ObserverWrapper<in T>>()
        private var isSticky: Boolean = false

        override fun observerActiveLevel(): Lifecycle.State {
            return if (lifecycleObserverAlwaysActive) Lifecycle.State.CREATED else Lifecycle.State.STARTED
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            runMainThread {
                val observerWrapper = ObserverWrapper(observer)
                if (!isSticky)
                    observerWrapper.preventNextEvent = version > ExternalLiveData.START_VERSION
                super.observe(owner, observer)
            }
        }

        override fun observeForever(observer: Observer<in T>) {
            runMainThread {
                val observerWrapper = ObserverWrapper(observer)
                if (!isSticky)
                    observerWrapper.preventNextEvent = version > ExternalLiveData.START_VERSION

                observerMap[observer] = observerWrapper
                super.observeForever(observer)
            }
        }

        fun observeSticky(owner: LifecycleOwner, observer: Observer<T>) {
            isSticky = true
            observe(owner, observer)
        }

        fun observeStickyForever(observer: Observer<T>) {
            isSticky = true
            observeForever(observer)
        }

        override fun removeObserver(observer: Observer<in T>) {
            var realObserver: Observer<in T>? = observer

            runMainThread {
                if (observerMap.containsKey(observer)) {
                    realObserver = observerMap.remove(observer)
                }
            }
            super.removeObserver(realObserver!!)

            if (autoClear && !this.hasObservers()) {
                val type = object : TypeToken<T>() {}.type
                LiveEventBus.bus.remove(type)
            }
        }
    }

    private class ObserverWrapper<T> internal constructor(private val observer: Observer<T>) : Observer<T> {
        var preventNextEvent = false

        override fun onChanged(t: T?) {
            if (preventNextEvent) {
                preventNextEvent = false
                return
            }
            try {
                observer.onChanged(t)
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }

        }
    }


    fun runMainThread(action: () -> Unit) {
        if (isMainThread()) {
            action()
        } else {
            mainHandler.post {
                action()
            }
        }
    }


}