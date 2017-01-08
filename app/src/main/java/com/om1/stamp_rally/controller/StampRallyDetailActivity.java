package com.om1.stamp_rally.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampRallyModel;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.event.FetchJsonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import database.entities.StampRallys;
import database.entities.Stamps;

public class StampRallyDetailActivity extends AppCompatActivity {
    private final EventBus eventBus = EventBus.getDefault();
    private StampRallys stampRally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_detail);
        eventBus.register(this);
        StampRallyModel stampRallyModel = StampRallyModel.getInstance();
        stampRallyModel.fetchJson();

    }

    //データベース通信処理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ","データベースとの通信に失敗");
            return;
        }
        try {
            //Jsonをオブジェクトに変換
            stampRally = new ObjectMapper().readValue(event.getJson(), StampRallys.class);
            Log.d("デバッグ","データベースとの通信に成功");

            for(Stamps stamps:stampRally.getStampsCollection()){
                System.out.println("デバッグ:詳細:" + stamps.getStampName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
