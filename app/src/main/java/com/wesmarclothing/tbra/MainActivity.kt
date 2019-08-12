package com.wesmarclothing.tbra

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wesmarclothing.kotlintools.kotlin.utils.async
import com.wesmarclothing.kotlintools.kotlin.utils.d
import com.wesmarclothing.kotlintools.kotlin.utils.main

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        ActivityScanerCode.start(this, ActivityScanerCode.QR_CODE)

//        ActivityScanerCode.setScanerListener(object : OnRxScanerListener {
//            override fun onSuccess(type: String?, result: Result?) {
//                Log.d("扫描结果：", result?.toString())
//            }
//
//            override fun onFail(type: String?, message: String?) {
//                Log.d("扫描结果：", message)
//            }
//        })

        async {
            Thread.currentThread().name.d()
            2 + 2
        } main {
            Thread.currentThread().name.d()
            it.d()
        }


    }
}
