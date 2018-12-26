package com.wesmarclothing.tbra;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.QuickContactBadge;

import com.vondear.rxtools.utils.RxUtils;
import com.vondear.rxtools.view.RxToast;


public class SplashActivity extends AppCompatActivity {

    boolean f = false;
    String TAG = "【SplashActivity】";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RxUtils.init(this.getApplication());

        RxToast.normal("开始");




        Chronometer ch_timer = findViewById(R.id.ch_timer);

        ch_timer.start();
        ch_timer.setBase(SystemClock.elapsedRealtime() + 100 * 1000);
        ch_timer.setFormat("%e");
        ch_timer.setCountDown(true);

        final ContentLoadingProgressBar progress = findViewById(R.id.progress);


        Log.d(TAG, "返在当前线程运行的毫秒数: " + SystemClock.currentThreadTimeMillis());
        Log.d(TAG, " 返回系统启动到现在的毫秒数，包含休眠时间。: " + SystemClock.elapsedRealtime());
        Log.d(TAG, "返回系统启动到现在的纳秒数，包含休眠时间。: " + SystemClock.elapsedRealtimeNanos());
        Log.d(TAG, "系统时间: " + System.currentTimeMillis());
        Log.d(TAG, "返回系统启动到现在的毫秒级时间，不包含休眠时间。（系统启动到现在的非休眠期时间）: " + SystemClock.uptimeMillis());


        ch_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f) {
                    f = !f;
                    progress.show();
                } else {
                    f = !f;
                    progress.hide();
                }
            }
        });

//        progress.getIndeterminateDrawable()
//                .setColorFilter(ContextCompat.getColor(this, R.color.colorAccent),
//                        PorterDuff.Mode.MULTIPLY);


        QuickContactBadge contact = findViewById(R.id.contact);

        contact.assignContactFromPhone("17665248850", true);

        dialog = new BottomSheetDialog(this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.bottom_sheet);


        View view = findViewById(R.id.ll_content_bottom_sheet);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e("Bottom Sheet Behaviour", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("Bottom Sheet Behaviour", "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

//        case R.id.btn_expand://展开
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        break;
//        case R.id.btn_collapsed://折叠
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        break;
//        case R.id.btn_hide://隐藏
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        break;


    }

    BottomSheetDialog dialog;

    public void toggle(View view) {
        dialog.show();

    }
}
