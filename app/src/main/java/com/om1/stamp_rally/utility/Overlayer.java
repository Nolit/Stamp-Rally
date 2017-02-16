package com.om1.stamp_rally.utility;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.om1.stamp_rally.R;

import static butterknife.ButterKnife.findById;

/**
 * Created by yaboo on 2017/02/17.
 */

public class Overlayer {
    private final float OVERLAY_ALPHA = 0.7f;
    Activity activity;

    public Overlayer(Activity activity){
        this.activity = activity;
    }

    public void showProgress(){
        FrameLayout overlayLayout = findById(activity, R.id.wait_overlay);
        overlayLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        overlayLayout.setAlpha(OVERLAY_ALPHA);
    }

    public void hideProgress(){
        FrameLayout overlayLayout = findById(activity, R.id.wait_overlay);
        overlayLayout.setOnTouchListener(null);
        overlayLayout.setAlpha(0);
    }

    public void showUploading(){

    }
}
