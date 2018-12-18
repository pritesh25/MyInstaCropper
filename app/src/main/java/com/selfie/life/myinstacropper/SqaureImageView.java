package com.selfie.life.myinstacropper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SqaureImageView extends ImageView {
    public SqaureImageView(Context context) {
        super(context);
    }

    public SqaureImageView(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    public SqaureImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
