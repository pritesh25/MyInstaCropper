package com.selfie.life.myinstacropper.Filter.Utils;




import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ToolbarLinearLayout extends LinearLayout {
    public ToolbarLinearLayout(Context context) {
        super(context);
    }

    public ToolbarLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolbarLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
