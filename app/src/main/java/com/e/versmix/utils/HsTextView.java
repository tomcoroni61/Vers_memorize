package com.e.versmix.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import com.e.versmix.R;

public class HsTextView extends androidx.appcompat.widget.AppCompatTextView{
    private int mHideTime=1700;
    public HsTextView(Context context) {
        super(context);
    }

    public HsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    public HsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        this.setVisibility(INVISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> setVisibility(VISIBLE), mHideTime);
        return super.performClick();
    }

}
