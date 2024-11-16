package com.e.versmix.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import com.e.versmix.R;

//abstract  https://developer.android.com/develop/ui/views/layout/custom-views/create-view?hl=de
public  class ShButton extends androidx.appcompat.widget.AppCompatButton {

    private int mHideTime=1700;

    public ShButton(Context context) {
        super(context, null, android.R.attr.buttonStyle);
    }

    public ShButton(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.buttonStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.shView,
                0, 0);

        try {
            mHideTime = a.getInteger(R.styleable.shView_hideTime, 1500);
        } finally {
            a.recycle();
        }
    }

    public ShButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        this.setVisibility(INVISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> setVisibility(VISIBLE), mHideTime);
        return super.performClick();
    }

//    @InspectableProperty
    public int getHideTime() {
        return mHideTime;
    }
    public void setHideTime(int hideTime) {
        mHideTime=hideTime;
    }
}
