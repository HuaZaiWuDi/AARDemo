package com.wesmarclothing.kotlintools.kotlin.utils

import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*

/**
 * @Package com.wesmarclothing.kotlintools.kotlin
 * @FileName Async
 * @Date 2019/7/11 16:46
 * @Author JACK
 * @Describe TODO
 * @Project WeiMiBra
 */


/**
 *  async {
 *  IO
 *} main {
 *  Main
 *}
 * 异步操作，
 * */
fun <T> LifecycleOwner.async(loader: () -> T): Deferred<T> {
    val deferred = GlobalScope.async(
            context = Dispatchers.IO, start = CoroutineStart.LAZY
    ) {
        loader()
    }
    lifecycle.addObserver(CoroutinesLifecycleListener(deferred))
    return deferred
}

infix fun <T> Deferred<T>.main(block: (T) -> Unit): Job {
    return GlobalScope.launch(context = Dispatchers.Main) {
        block(this@main.await())
    }
}

class CoroutinesLifecycleListener(val deferred: Deferred<*>) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun cancelCoroutine() {
        if (!deferred.isCancelled) {
            deferred.cancel()
        }
    }
}

fun isMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}

