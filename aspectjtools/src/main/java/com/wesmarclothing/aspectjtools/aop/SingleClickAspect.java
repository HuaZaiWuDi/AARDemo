package com.wesmarclothing.aspectjtools.aop;

import android.view.View;

import com.wesmarclothing.aspectjtools.util.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Calendar;

/**
 * 实例
 * Created by baixiaokang on 16/12/9.
 * 防止View被连续点击,间隔时间600ms
 */

@Aspect
public class SingleClickAspect {
    static int TIME_TAG = Integer.MAX_VALUE;
    public static final int MIN_CLICK_DELAY_TIME = 600;

    //https://upload-images.jianshu.io/upload_images/4574229-25d3e3da7b7ad5cf?imageMogr2/auto-orient/strip%7CimageView2/2/w/800/format/webp
    @Pointcut("execution(@com.wesmarclothing.aspectjtools.annotation.SingleClick * *(..))")//方法切入点
    public void methodAnnotated() {
    }

    @Around("methodAnnotated()")//在连接点进行方法替换
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        View view = null;
        for (Object arg : joinPoint.getArgs())
            if (arg instanceof View) view = (View) arg;
        if (view != null) {
            Object tag = view.getTag(TIME_TAG);
            long lastClickTime = ((tag != null) ? (long) tag : 0);
            Logger.d("SingleClickAspect", "lastClickTime:" + lastClickTime);
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {//过滤掉600毫秒内的连续点击
                view.setTag(TIME_TAG, currentTime);
                Logger.d("SingleClickAspect", "currentTime:" + currentTime);
                joinPoint.proceed();//执行原方法
            }
        }
    }
}
