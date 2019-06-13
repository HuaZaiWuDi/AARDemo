package com.vondear.rxtools.interfaces;

/**
 * @Package com.wesmart.magnetictherapy.utils
 * @FileName ILog
 * @Date 2019/6/12 17:25
 * @Author JACK
 * @Describe TODO
 * @Project xuancililiao
 */
public interface ILog {

    void log(char level, String tag, String message, Throwable throwable);
}
