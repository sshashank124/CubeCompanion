package com.qbix.cubecompanion;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class SquareView extends View {

    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}