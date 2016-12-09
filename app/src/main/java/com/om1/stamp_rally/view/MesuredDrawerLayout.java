package com.om1.stamp_rally.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by yaboo on 2016/12/04.
 */

public class MesuredDrawerLayout extends DrawerLayout {

    public MesuredDrawerLayout(Context context) {
        super(context);
    }

    public MesuredDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MesuredDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}