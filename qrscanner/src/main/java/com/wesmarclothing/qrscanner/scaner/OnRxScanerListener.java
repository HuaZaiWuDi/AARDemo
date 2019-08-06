package com.wesmarclothing.qrscanner.scaner;


import android.app.Activity;

import com.google.zxing.Result;

/**
 * @author Vondear
 * @date 2017/9/22
 */

public interface OnRxScanerListener {
    void onSuccess(String type, Result result, Activity activity);

    void onFail(String type, String message);
}
