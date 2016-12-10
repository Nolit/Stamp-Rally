package com.om1.stamp_rally.controller;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.event.StampEvent;
import com.om1.stamp_rally.utility.ByteConverter;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.dbadapter.StampDbAdapter;
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

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
                Integer stampRallyId = Integer.valueOf(getIntent().getIntExtra("stampRallyId", 0));
                Integer stampId = Integer.valueOf(getIntent().getIntExtra("stampId", 0));
                Double latitude = Double.valueOf(getIntent().getLongExtra("latitude", 0));
                Double longitude = Double.valueOf(getIntent().getLongExtra("longitude", 0));

                EventBusUtil.defaultBus.post(new StampEvent(stampId, stampRallyId, latitude, longitude));
            }
        });
    }
}
