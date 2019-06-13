package com.wesmarclothing.aspectjtools.aop;

import com.wesmarclothing.aspectjtools.annotation.Permission;
import com.wesmarclothing.aspectjtools.util.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 实例
 * Created by baixiaokang on 17/1/31.
 * 申请系统权限切片，根据注解值申请所需运行权限
 */
@Aspect
public class SysPermissionAspect {

    @Around("execution(@com.wesmarclothing.aspectjtools.annotation.Permission * *(..)) && @annotation(permission)")
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint, Permission permission) throws Throwable {
        Logger.d("权限", "aroundJoinPoint");
//        AppCompatActivity ac = (AppCompatActivity) App.getAppContext().getCurActivity();
//        MPermissionUtils.requestPermissionsResult(ac, 1, permission.value()
//                , new MPermissionUtils.OnPermissionListener() {
//                    @Override
//                    public void onPermissionGranted() {
//                        Log.d("权限", "onPermissionGranted");
//                        try {
//                            joinPoint.proceed();//获得权限，执行原方法
//                        } catch (Throwable e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionDenied() {
//                        MPermissionUtils.showTipsDialog(ac);
//                    }
//                });
    }
}


