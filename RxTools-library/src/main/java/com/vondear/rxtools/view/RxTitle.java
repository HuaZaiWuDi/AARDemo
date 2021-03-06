package com.vondear.rxtools.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vondear.rxtools.R;
import com.vondear.rxtools.utils.RxDataUtils;
import com.vondear.rxtools.utils.RxUtils;
import com.vondear.rxtools.utils.bitmap.RxImageUtils;
import com.vondear.rxtools.utils.RxKeyboardUtils;
import com.vondear.rxtools.view.textview.RxTextAutoZoom;

/**
 * @author by vondear on 2017/1/2.
 */

public class RxTitle extends FrameLayout {
    //*******************************************控件start******************************************
    private RelativeLayout mRootLayout;//根布局

    private RxTextAutoZoom mTvTitle;//Title的TextView控件

    private LinearLayout mLlLeft;//左边布局

    private ImageView mIvLeft;//左边ImageView控件

    private TextView mTvLeft;//左边TextView控件

    private LinearLayout mLlRight;//右边布局

    private ImageView mIvRight;//右边ImageView控件

    private TextView mTvRight;//右边TextView控件
    //===========================================控件end=============================================

    //*******************************************属性start*******************************************
    private String mTitle;//Title文字

    private int mTitleColor;//Title字体颜色

    private int mTitleSize;//Title字体大小


    private int mLeftIcon;//左边 ICON 引用的资源ID

    private int mRightIcon;//右边 ICON 引用的资源ID

    private boolean mLeftIconVisibility;//左边 ICON 是否显示

    private boolean mRightIconVisibility;//右边 ICON 是否显示

    private String mLeftText;//左边文字

    private int mLeftTextColor;//左边字体颜色

    private int mLeftTextSize;//左边字体大小

    private boolean mLeftTextVisibility;//左边文字是否显示

    private String mRightText;//右边文字

    private int mRightTextColor;//右边字体颜色

    private int mRightTextSize;//右边字体大小

    private boolean mRightTextVisibility;//右边文字是否显示
    //===========================================属性end=============================================

    public RxTitle(Context context) {
        super(context);
    }

    public RxTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        //导入布局
        initView(context, attrs);
    }

    private void initView(final Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.include_rx_title, this);

        mRootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        mTvTitle = (RxTextAutoZoom) findViewById(R.id.tv_rx_title);
        mLlLeft = (LinearLayout) findViewById(R.id.ll_left);
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        mLlRight = (LinearLayout) findViewById(R.id.ll_right);
        mTvLeft = (TextView) findViewById(R.id.tv_left);
        mTvRight = (TextView) findViewById(R.id.tv_right);

        //获得这个控件对应的属性。
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RxTitle);

        try {
            //获得属性值
            //getColor(R.styleable.RxTitle_RxBackground, getResources().getColor(R.color.transparent))
            mTitle = a.getString(R.styleable.RxTitle_title);//标题
            if (RxDataUtils.isNullString(mTitle)) mTitle = "";
            mTitleColor = a.getColor(R.styleable.RxTitle_titleColor, getResources().getColor(R.color.white));//标题颜色
            mTitleSize = a.getDimensionPixelSize(R.styleable.RxTitle_titleSize, 60);//标题字体大小
            //TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics())

            mLeftIcon = a.getResourceId(R.styleable.RxTitle_leftIcon, R.drawable.previous_icon);//左边图标
            mRightIcon = a.getResourceId(R.styleable.RxTitle_rightIcon, 0);//右边图标
            mLeftIconVisibility = a.getBoolean(R.styleable.RxTitle_leftIconVisibility, true);//左边图标是否显示

            mLeftText = a.getString(R.styleable.RxTitle_leftText);
            if (RxDataUtils.isNullString(mLeftText)) mLeftText = "";
            mLeftTextColor = a.getColor(R.styleable.RxTitle_leftTextColor, getResources().getColor(R.color.white));//左边字体颜色
            mLeftTextSize = a.getDimensionPixelSize(R.styleable.RxTitle_leftTextSize, 48);//标题字体大小

            mRightText = a.getString(R.styleable.RxTitle_rightText);
            if (RxDataUtils.isNullString(mRightText)) mRightText = "";
            mRightTextColor = a.getColor(R.styleable.RxTitle_rightTextColor, getResources().getColor(R.color.white));//右边字体颜色
            mRightTextSize = a.getDimensionPixelSize(R.styleable.RxTitle_rightTextSize, 48);//标题字体大小

        } finally {
            //回收这个对象
            a.recycle();
        }

        //******************************************************************************************以下属性初始化


        setTitle(mTitle);
        setTitleColor(mTitleColor);
        setTitleSize(mTitleSize);


//        leftIcon
        if (mLeftIconVisibility) {
            setLeftIcon(mLeftIcon);
        }
        setLeftIconVisibility(mLeftIconVisibility);

//        rightIcon
        mRightIconVisibility = mRightIcon != 0;

        if (mRightIconVisibility) {
            setRightIcon(mRightIcon);
        }
        setRightIconVisibility(mRightIconVisibility);


//        leftText
        mLeftTextVisibility = !RxDataUtils.isNullString(mLeftText);
        if (mLeftTextVisibility) {
            setLeftText(mLeftText);
            setLeftTextColor(mLeftTextColor);
            setLeftTextSize(mLeftTextSize);
        }
        setLeftTextVisibility(mLeftTextVisibility);

//        rightText
        mRightTextVisibility = !RxDataUtils.isNullString(mRightText);
        if (mRightTextVisibility) {
            setRightText(mRightText);
            setRightTextColor(mRightTextColor);
            setRightTextSize(mRightTextSize);
        }
        setRightTextVisibility(mRightTextVisibility);


        initAutoFitEditText();
        //==========================================================================================以上为属性初始化
    }

    private void initAutoFitEditText() {

        mTvTitle.clearFocus();
        mTvTitle.setEnabled(false);
        mTvTitle.setFocusableInTouchMode(false);
        mTvTitle.setFocusable(false);
        mTvTitle.setEnableSizeCache(false);
        //might cause crash on some devices
        mTvTitle.setMovementMethod(null);
        // can be added after layout inflation;
        mTvTitle.setMaxHeight(RxUtils.dp2px(55));
        //don't forget to add min text size programmatically
        mTvTitle.setMinTextSize(37f);

        try {
            RxTextAutoZoom.setNormalization((Activity) getContext(), mRootLayout, mTvTitle);
            RxKeyboardUtils.hideSoftInput((Activity) getContext());
        } catch (Exception e) {

        }

    }

    //**********************************************************************************************以下为get方法

    public RelativeLayout getRootLayout() {
        return mRootLayout;
    }

    public RxTextAutoZoom getTvTitle() {
        return mTvTitle;
    }

    public LinearLayout getLlLeft() {
        return mLlLeft;
    }

    public ImageView getIvLeft() {
        return mIvLeft;
    }

    public TextView getTvLeft() {
        return mTvLeft;
    }

    public LinearLayout getLlRight() {
        return mLlRight;
    }

    public ImageView getIvRight() {
        return mIvRight;
    }

    public TextView getTvRight() {
        return mTvRight;
    }


    public String getLeftText() {
        return mLeftText;
    }

    public int getLeftTextColor() {
        return mLeftTextColor;
    }

    public int getLeftTextSize() {
        return mLeftTextSize;
    }

    public boolean isLeftTextVisibility() {
        return mLeftTextVisibility;
    }

    public String getRightText() {
        return mRightText;
    }

    public int getRightTextColor() {
        return mRightTextColor;
    }

    public int getRightTextSize() {
        return mRightTextSize;
    }

    public boolean isRightTextVisibility() {
        return mRightTextVisibility;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getTitleColor() {
        return mTitleColor;
    }

    public int getTitleSize() {
        return mTitleSize;
    }

    public int getLeftIcon() {
        return mLeftIcon;
    }

    public int getRightIcon() {
        return mRightIcon;
    }

    public boolean isLeftIconVisibility() {
        return mLeftIconVisibility;
    }

    public boolean isRightIconVisibility() {
        return mRightIconVisibility;
    }

    //==============================================================================================以上为get方法

    //**********************************************************************************************以下为set方法

    public RxTitle setLeftFinish(final Activity activity) {
        mLlLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
        return this;
    }


    public RxTitle setLeftOnClickListener(OnClickListener onClickListener) {
        mLlLeft.setOnClickListener(onClickListener);
        return this;
    }

    public RxTitle setRightOnClickListener(OnClickListener onClickListener) {
        mLlRight.setOnClickListener(onClickListener);
        return this;
    }

    public RxTitle setLeftTextOnClickListener(OnClickListener onClickListener) {
        mTvLeft.setOnClickListener(onClickListener);
        return this;
    }

    public RxTitle setRightTextOnClickListener(OnClickListener onClickListener) {
        mTvRight.setOnClickListener(onClickListener);
        return this;
    }

    public RxTitle setLeftIconOnClickListener(OnClickListener onClickListener) {
        mIvLeft.setOnClickListener(onClickListener);
        return this;
    }

    public RxTitle setRightIconOnClickListener(OnClickListener onClickListener) {
        mIvRight.setOnClickListener(onClickListener);
        return this;
    }

    //**********************************************************************************************以下为Title相关方法
    public RxTitle setTitle(String title) {
        mTitle = title;
        mTvTitle.setText(mTitle);
        return this;
    }

    public RxTitle setTitleColor(int titleColor) {
        mTitleColor = titleColor;
        mTvTitle.setTextColor(mTitleColor);
        return this;
    }

    public RxTitle setTitleSize(int titleSize) {
        mTitleSize = titleSize;
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
        return this;
    }

    //==============================================================================================以上为  Title  相关方法


    //**********************************************************************************************以下为  左边文字  相关方法
    public RxTitle setLeftText(String leftText) {
        mLeftText = leftText;
        mTvLeft.setText(mLeftText);
        return this;
    }

    public RxTitle setLeftTextColor(int leftTextColor) {
        mLeftTextColor = leftTextColor;
        mTvLeft.setTextColor(mLeftTextColor);
        return this;
    }

    public RxTitle setLeftTextSize(int leftTextSize) {
        mLeftTextSize = leftTextSize;
        mTvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLeftTextSize);
        return this;
    }

    public RxTitle setLeftTextVisibility(boolean leftTextVisibility) {
        mLeftTextVisibility = leftTextVisibility;
        if (mLeftTextVisibility) {
            mTvLeft.setVisibility(VISIBLE);
        } else {
            mTvLeft.setVisibility(GONE);
        }
        return this;
    }
    //==============================================================================================以上为  左边文字  相关方法

    //**********************************************************************************************以下为  右边文字  相关方法
    public RxTitle setRightText(String rightText) {
        mRightText = rightText;
        mTvRight.setText(mRightText);
        return this;
    }

    public RxTitle setRightTextColor(int rightTextColor) {
        mRightTextColor = rightTextColor;
        mTvRight.setTextColor(mRightTextColor);
        return this;
    }

    public RxTitle setRightTextSize(int rightTextSize) {
        mRightTextSize = rightTextSize;
        mTvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize);
        return this;
    }

    public RxTitle setRightTextVisibility(boolean rightTextVisibility) {
        mRightTextVisibility = rightTextVisibility;
        if (mRightTextVisibility) {
            mTvRight.setVisibility(VISIBLE);
        } else {
            mTvRight.setVisibility(GONE);
        }
        return this;
    }
    //==============================================================================================以上为  右边文字  相关方法


    public RxTitle setLeftIcon(int leftIcon) {
        mLeftIcon = leftIcon;
        mIvLeft.setImageResource(mLeftIcon);
        return this;
    }

    public RxTitle setLeftIconVisibility(boolean leftIconVisibility) {
        mLeftIconVisibility = leftIconVisibility;
        if (mLeftIconVisibility) {
            mIvLeft.setVisibility(VISIBLE);
        } else {
            mIvLeft.setVisibility(GONE);
        }
        return this;
    }

    public RxTitle setRightIcon(int rightIcon) {
        mRightIcon = rightIcon;
        mIvRight.setImageResource(mRightIcon);
        return this;
    }

    public RxTitle setRightIconVisibility(boolean rightIconVisibility) {
        mRightIconVisibility = rightIconVisibility;
        if (mRightIconVisibility) {
            mIvRight.setVisibility(VISIBLE);
        } else {
            mIvRight.setVisibility(GONE);
        }
        return this;
    }
    //==============================================================================================以上为set方法

}
