package com.selfie.life.myinstacropper;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Pritesh on 3/21/2018.
 */

public class MySquareImageView extends ImageView {
    public MySquareImageView(Context context) {
        super(context);
    }

    public MySquareImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MySquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
