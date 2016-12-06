package com.om1.stamp_rally.controller;

import android.database.Cursor;
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

public class TakeStampActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        dbProcess();
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


    private void dbProcess(){
        StampRallyDbAdapter adapter = new StampRallyDbAdapter(this);
        adapter.open();

        Log.d("adapter ", "test1");
        cTest(adapter.getAll());

        adapter.createStampRally(5, 9);
        cTest(adapter.getAll());
        Log.d("adapter ", "test2");

        StampDbAdapter stampAdapter = new StampDbAdapter(this);
    }

    private void cTest(Cursor c){
        startManagingCursor(c);
        if(c.moveToFirst()){
            do {
                Log.d("id: ", c.getString(c.getColumnIndex("id")));
                Log.d("size: ", ""+c.getInt(c.getColumnIndex("size")));
            }while(c.moveToNext());
        }
    }
}
