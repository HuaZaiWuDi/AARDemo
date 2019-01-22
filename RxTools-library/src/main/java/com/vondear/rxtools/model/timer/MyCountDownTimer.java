package com.vondear.rxtools.model.timer;

import android.os.CountDownTimer;

/**
 * @Package com.vondear.rxtools.model.timer
 * @FileName MyCountDownTimer
 * @Date 2019/1/21 17:33
 * @Author JACK
 * @Describe TODO
 * @Project WeiMiBra
 */
public class MyCountDownTimer {

    /**
     * 调用startTimer()和stopTimer()实行暂停重启倒计时。
     */
    private CountDownTimer countDownTimer;


    private MyCountDownTimer() {
    }

    /**
     * 开启倒计时
     */
    private void startTimer() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(20000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                }
            };
        }
        countDownTimer.start();
    }




    /**
     * 结束倒计时
     */
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
