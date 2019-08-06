package com.wesmarclothing.tbra

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.zxing.Result
import com.wesmarclothing.qrscanner.ActivityScanerCode
import com.wesmarclothing.qrscanner.scaner.OnRxScanerListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityScanerCode.start(this, ActivityScanerCode.QR_CODE)

        ActivityScanerCode.setScanerListener(object : OnRxScanerListener {
            override fun onSuccess(type: String?, result: Result?) {
                Log.d("扫描结果：", result?.toString())
            }

            override fun onFail(type: String?, message: String?) {
                Log.d("扫描结果：", message)
            }

        })
    }
}
