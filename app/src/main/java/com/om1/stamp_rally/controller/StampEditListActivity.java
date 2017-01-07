package com.om1.stamp_rally.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.adapter.StampEditListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.om1.stamp_rally.utility.dbadapter.StampDbAdapter;

import database.entities.Stamps;

public class StampEditListActivity extends AppCompatActivity {
    StampEditListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_edit_list);

        List<Stamps> stampDataList = loadStampData();
        adapter = new StampEditListAdapter(this, 0, stampDataList);

        ListView listView = (ListView)findViewById(R.id.stampEditlistView);
        listView.setAdapter(adapter);

    }

    private List<Stamps> loadStampData(){
        List<Map<String, Object>> stampMapList = new StampDbAdapter(this).getAllAsList();
        Log.d("スタンプラリー", stampMapList.toString());
        List<Stamps> stampList = new ArrayList<>();
        for(Map<String, Object> stampMap : stampMapList){
            Stamps stampData = convertMapToStamp(stampMap);
            stampList.add(stampData);
        }
        Log.d("スタンプラリー", stampList.toString());

        return stampList;
    }

    private Stamps convertMapToStamp(Map<String, Object> stampMap){
        String title = (String) stampMap.get("title");
        byte[] picture = (byte[]) stampMap.get("picture");

        Stamps stamp = new Stamps();
        stamp.setStampName(title);
        stamp.setPicture(picture);

        return stamp;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private Bitmap getImage(){
        return BitmapFactory.decodeResource(getResources(), R.drawable.common_full_open_on_phone);
    }
}
