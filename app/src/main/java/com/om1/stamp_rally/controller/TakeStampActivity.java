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
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.dbadapter.StampDbAdapter;
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

public class TakeStampActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        stampRallyProcess();
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


    private void stampRallyProcess(){
        StampRallyDbAdapter adapter = new StampRallyDbAdapter(this);
        adapter.open();

        adapter.tryStampRally(1);
//        adapter.createStampRally(9);

        logStampRally(adapter.getAll());
        logStampRally(adapter.getTryingStampRally());
    }

    private void logStampRally(Cursor c){
        startManagingCursor(c);
        if(c.moveToFirst()){
            do {
                Log.d("id: ", c.getString(c.getColumnIndex("id")));
                Log.d("size: ", ""+c.getInt(c.getColumnIndex("size")));
                Log.d("isChallenge: ", ""+c.getInt(c.getColumnIndex("isChallenge")));
            }while(c.moveToNext());
        }
    }

    private void stampProcess(){
        StampDbAdapter adapter = new StampDbAdapter(this);
        adapter.open();
//        adapter.dropTable();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_setting_dark);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        adapter.createStamp(1, "スタンプ1", "メモ1", b);
        logStamp(adapter.getAll());
    }

    private void logStamp(Cursor c){
        startManagingCursor(c);
        if(c.moveToFirst()){
            do {
                Log.d("id: ", ""+c.getInt(c.getColumnIndex("id")));
                Log.d("stampRallyId: ", ""+c.getInt(c.getColumnIndex("stampRallyId")));
                Log.d("title: ", ""+c.getString(c.getColumnIndex("title")));
                Log.d("memo: ", ""+c.getString(c.getColumnIndex("memo")));
                Log.d("picture: ", ""+c.getBlob(c.getColumnIndex("picture")));
                Log.d("create_time: ", ""+new SimpleDateFormat("yyyy/MM/dd HH:mm").format(c.getLong(c.getColumnIndex("create_time"))));
            }while(c.moveToNext());
        }
    }
}
