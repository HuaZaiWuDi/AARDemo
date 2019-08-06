package com.wesmarclothing.tbra

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.wesmarclothing.qrscanner.ActivityScanerCode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityScanerCode.start(this, ActivityScanerCode.QR_CODE)

//        ActivityScanerCode.setScanerListener(object : OnRxScanerListener {
//            override fun onSuccess(type: String?, result: Result?) {
//                Log.d("扫描结果：", result?.toString())
//            }
//
//            override fun onFail(type: String?, message: String?) {
//                Log.d("扫描结果：", message)
//            }
//        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v("扫描", "requestCode:$requestCode")
        Log.v("扫描", "resultCode:$resultCode")
        Log.v("扫描", "data:$data")
        if (requestCode == ActivityScanerCode.BUNDLE_RESULT_CODE) {
            val extra = data?.getStringExtra(ActivityScanerCode.BUNDLE_RESULT_DATA)
            Log.v("扫描", "extra:$extra")
        }

    }

}
