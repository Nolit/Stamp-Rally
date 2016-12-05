package com.om1.stamp_rally.controller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.controller.view.CameraView;
import com.om1.stamp_rally.model.event.StampEvent;
import com.om1.stamp_rally.utility.EventBusUtil;

public class TakeStampActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageButton ib = (ImageButton) findViewById(R.id.stamp_button);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBusUtil.defaultBus.post(new StampEvent());
            }
        });
    }
}
