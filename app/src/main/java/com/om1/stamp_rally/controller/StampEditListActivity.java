package com.om1.stamp_rally.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.adapter.StampEditListAdapter;

import java.util.ArrayList;

import com.om1.stamp_rally.model.adapter.StampEditListAdapter.StampData;

public class StampEditListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_edit_list);

        ArrayList<StampData> stampDataList = new ArrayList<>();
        StampEditListAdapter adapter = new StampEditListAdapter(this, 0, stampDataList);

        StampData stampData = adapter.createData();
        stampData.setTitle("hello-title");
        stampData.setThumbnail(getImage());
        stampDataList.add(stampData);

        ListView listView = (ListView)findViewById(R.id.stampEditlistView);
        listView.setAdapter(adapter);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private Bitmap getImage(){
        return BitmapFactory.decodeResource(getResources(), R.drawable.common_full_open_on_phone);
    }
}
