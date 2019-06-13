package com.wesmarclothing.aspectjtools.util;

import android.util.Log;

/**
 * @Package com.wesmarclothing.aspectjtools.util
 * @FileName Logger
 * @Date 2019/6/12 16:57
 * @Author JACK
 * @Describe TODO
 * @Project WeiMiBra
 */
public class Logger {

    private static final String TAG = "Logger";
    private static Logger mLogger;
    private static ILog log;

    private Logger() {
    }

    public static void setLog(ILog log) {
        Logger.log = log;
    }

    public static void d(String tag, String message) {
        log("d", tag, message, null);
    }

    public static void d(String message) {
        log("d", TAG, message, null);
    }

    public static void i(String tag, String message) {
        log("i", tag, message, null);
    }

    public static void i(String message) {
        log("i", TAG, message, null);
    }


    public static void w(String tag, String message) {
        log("i", tag, message, null);
    }

    public static void w(String message) {
        log("i", TAG, message, null);
    }

    public static void v(String tag, String message) {
        log("i", tag, message, null);
    }


    public static void v(String message) {
        log("i", TAG, message, null);
    }

    public static void e(String tag, String message, Throwable throwable) {
        log("i", tag, message, throwable);
    }


    public static void e(String message, Throwable throwable) {
        log("i", TAG, message, throwable);
    }

    public static void e(Throwable throwable) {
        log("i", TAG, "", throwable);
    }


    public static void log(String level, String tag, String message, Throwable throwable) {
        if (log != null)
            log.log(level, tag, message, throwable);

        switch (level) {
            case "d":
                Log.d(tag, message);
                break;
            case "i":
                Log.i(tag, message);
                break;
            case "v":
                Log.v(tag, message);
                break;
            case "w":
                Log.w(tag, message);
                break;
            case "e":
                Log.e(tag, message, throwable);
                break;
        }
    }

}
