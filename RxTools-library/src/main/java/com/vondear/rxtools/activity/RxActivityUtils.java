package com.vondear.rxtools.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.vondear.rxtools.utils.RxIntentUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;


/**
 * Created by vondear on 2016/1/24.
 * 封装Activity相关工具类
 */
public class RxActivityUtils {

    private static Stack<Activity> activityStack;

    /**
     * 添加Activity 到栈
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }


    public static void removeActivity(Activity activity) {
        if (activityStack != null && activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 获取当前的Activity（堆栈中最后一个压入的)
     */
    public static Activity currentActivity() {
        Activity activity = null;
        if (!activityStack.empty()) {
            activity = activityStack.lastElement();
        }
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public static void finishActivity() {
        if (!activityStack.empty()) {
            Activity activity = activityStack.lastElement();
            finishActivity(activity);
        }
    }


    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activityAnim(activity);
            activity = null;
        }
    }


    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        //防止并发异常，使用迭代器遍历
        Iterator<Activity> activitys = activityStack.iterator();
        while (activitys.hasNext()) {
            Activity activity = activitys.next();
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }


    /**
     * 结束多个界面
     *
     * @param cls
     */
    public static void finishMultiAvtivity(Class<?>... cls) {
        //防止并发异常，使用迭代器遍历
        Iterator<Activity> activitys = activityStack.iterator();
        while (activitys.hasNext()) {
            Activity activity = activitys.next();
            for (Class<?> finishAc : cls) {
                if (activity.getClass().equals(finishAc)) {
                    finishActivity(activity);
                }
            }
        }
    }

    /**
     * 结束所有的Activity、
     */
    public static void finishAllActivity() {
        int size = activityStack.size();
        for (int i = 0; i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public static void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {

        }
    }

    public static Stack<Activity> getActivityStack() {
        return activityStack;
    }

    /**
     * 判断是否存在指定Activity
     *
     * @param context   上下文
     * @param className activity全路径类名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isExistActivity(Context context, String className) {
        Intent intent = new Intent();
        intent.setClassName(context.getPackageName(), className);
        return !(context.getPackageManager().resolveActivity(intent, 0) == null ||
                intent.resolveActivity(context.getPackageManager()) == null ||
                context.getPackageManager().queryIntentActivities(intent, 0).size() == 0);
    }

    /**
     * 判断是否存在指定Activity
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isExistActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断界面是否在顶部
     *
     * @param cls
     * @return
     */
    public static boolean isTopActivity(Class<?> cls) {
        if (currentActivity().getClass().equals(cls)) {
            return true;
        }
        return false;
    }


    /**
     * 打开指定的Activity
     *
     * @param context     上下文
     * @param packageName 包名
     * @param className   全类名
     */
    public static void launchActivity(Context context, String packageName, String className) {
        launchActivity(context, packageName, className, null);
    }

    /**
     * 打开指定的Activity
     *
     * @param context     上下文
     * @param packageName 包名
     * @param className   全类名
     * @param bundle      bundle
     */
    public static void launchActivity(Context context, String packageName, String className, Bundle bundle) {
        context.startActivity(RxIntentUtils.getComponentNameIntent(packageName, className, bundle));
    }

    /**
     * 要求最低API为11
     * Activity 跳转
     * 跳转后Finish之前所有的Activity
     *
     * @param context
     * @param goal
     */
    public static void skipActivityAndFinishAll(Context context, Class<?> goal, Bundle bundle) {
        Intent intent = new Intent(context, goal);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        activityAnim(context);
    }

    /**
     * 要求最低API为11
     * Activity 跳转
     * 跳转后Finish之前所有的Activity
     *
     * @param context
     * @param goal
     */
    public static void skipActivityAndFinishAll(Context context, Class<?> goal) {
        Intent intent = new Intent(context, goal);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        activityAnim(context);
    }


    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    public static void skipActivityAndFinish(Context context, Class<?> goal, Bundle bundle) {
        Intent intent = new Intent(context, goal);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(bundle);
        context.startActivity(intent);
        activityAnim(context);
        ((Activity) context).finish();
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    public static void skipActivityAndFinish(Context context, Class<?> goal) {
        Intent intent = new Intent(context, goal);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
        activityAnim(context);
        ((Activity) context).finish();
    }


    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    public static void skipActivity(Context context, Class<?> goal) {
        Intent intent = new Intent(context, goal);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
        activityAnim(context);
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    public static void skipActivity(Context context, Class<?> goal, Bundle bundle) {
        Intent intent = new Intent(context, goal);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(bundle);
        context.startActivity(intent);
        activityAnim(context);
    }

    /**
     * Activity 跳转
     * 直接启动栈内实例，没有则正常启动
     * <p>
     * 如果在intent里设置交给 startActivity（）,
     * 这个flag会把已经运行过的acivity带到task历史栈的顶端。
     * 例如，一个task由A,B,C,D四个activity组成，
     * 如果D携带这个flag的intent调用startActivity()打开B，
     * 那么B就会被带到历史栈的前部，结果是:A,C,D,B.如果LAG_ACTIVITY_CLEAR_TOP 被设置，
     * 那么FLAG_ACTIVITY_REORDER_TO_FRONT将被忽略。
     *
     * @param context
     * @param goal
     */
    public static void skipActivityTop(Context context, Class<?> goal) {
        Intent intent = new Intent(context, goal);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
        activityAnim(context);
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    public static void skipActivityTop(Context context, Class<?> goal, Bundle bundle) {
        Intent intent = new Intent(context, goal);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtras(bundle);
        context.startActivity(intent);
        activityAnim(context);
    }

    /**
     * Activity 跳转
     * 栈顶复用，没有则正常启动
     *
     * @param context
     * @param goal
     */
    public static void skipActivitySingleTop(Context context, Class<?> goal) {
        Intent intent = new Intent(context, goal);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
        activityAnim(context);
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    public static void skipActivitySingleTop(Context context, Class<?> goal, Bundle bundle) {
        Intent intent = new Intent(context, goal);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(bundle);
        context.startActivity(intent);
        activityAnim(context);
    }


    /**
     * Activity 跳转
     * 非Activity实例启动，如Application，Dialog，广播等等启动
     *
     * @param context
     * @param goal
     */
    public static void skipActivityNotActivity(Context context, Class<?> goal) {
        Intent intent = new Intent(context, goal);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        activityAnim(context);
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    public static void skipActivityNotActivity(Context context, Class<?> goal, Bundle bundle) {
        Intent intent = new Intent(context, goal);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        context.startActivity(intent);
        activityAnim(context);
    }


    public static void skipActivityForResult(Activity context, Class<?> goal, int requestCode) {
        Intent intent = new Intent(context, goal);
        context.startActivityForResult(intent, requestCode);
        activityAnim(context);
    }

    public static void skipActivityForResult(Activity context, Class<?> goal, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, goal);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
        activityAnim(context);
    }


    /**
     * 获取launcher activity
     *
     * @param context     上下文
     * @param packageName 包名
     * @return launcher activity
     */
    public static String getLauncherActivity(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : infos) {
            if (info.activityInfo.packageName.equals(packageName)) {
                return info.activityInfo.name;
            }
        }
        return "no " + packageName;
    }


    /**
     * 跳转到桌面
     *
     * @param context
     * @return
     */
    public static String getLauncherActivity(@NonNull Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo aInfo : info) {
            if (aInfo.activityInfo.packageName.equals(context.getPackageName())) {
                return aInfo.activityInfo.name;
            }
        }
        return null;
    }

    /**
     * 回到桌面 -> 同点击Home键效果
     */
    public static void startHomeActivity(@NonNull Context context) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(homeIntent);
        activityAnim(context);
    }


    /**
     * 设置界面跳转动画
     *
     * @param context
     */
    private static void activityAnim(final Context context) {
//        ((Activity) context).overridePendingTransition(R.anim.translate_left2right, R.anim.translate_right2left);
    }


    /**
     * 设置跳转动画
     *
     * @param context
     * @param enterAnim
     * @param exitAnim
     * @return
     */
    private static Bundle getOptionsBundle(final Context context, final int enterAnim, final int exitAnim) {
        return ActivityOptionsCompat.makeCustomAnimation(context, enterAnim, exitAnim).toBundle();
    }

    /**
     * 设置跳转动画
     *
     * @param activity
     * @param sharedElements
     * @return
     */
    private static Bundle getOptionsBundle(final Activity activity, final View[] sharedElements) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int len = sharedElements.length;
            @SuppressWarnings("unchecked")
            Pair<View, String>[] pairs = new Pair[len];
            for (int i = 0; i < len; i++) {
                pairs[i] = Pair.create(sharedElements[i], sharedElements[i].getTransitionName());
            }
            return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs).toBundle();
        }
        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, null, null).toBundle();
    }


}
