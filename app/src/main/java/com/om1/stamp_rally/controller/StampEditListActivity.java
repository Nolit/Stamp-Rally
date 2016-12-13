package com.om1.stamp_rally.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampRallyModel;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.bean.StampListAdapter;
import com.om1.stamp_rally.model.event.FetchJsonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import database.entities.StampRallys;
import database.entities.Stamps;

public class StampEditListActivity extends AppCompatActivity {
    private final EventBus eventBus = EventBus.getDefault();
    private StampRallys stampRally = null; //データベース
    StampBean stampBean;
    ArrayList<StampBean> stampList;
    ListView listView;
    StampListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_edit_list);
        eventBus.register(this);

        listView = (ListView)findViewById(R.id.stampEditlistView);
        stampBean = new StampBean();
        stampList = new ArrayList<StampBean>();
        adapter = new StampListAdapter(StampEditListActivity.this);
        adapter.setStampList(stampList);
        listView.setAdapter(adapter);

        StampRallyModel stampRallyModel = StampRallyModel.getInstance();
        stampRallyModel.fetchJson();    //データベースと通信

    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(this);
    }

    //データベース通信処理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ","データベースとの通信に失敗");
            return;
        }
        try {
            stampRally = new ObjectMapper().readValue(event.getJson(), StampRallys.class);
            Log.d("デバッグ","StampEditListActivity:データベースとの通信に成功");

            //ListViewの項目追加
            if(stampRally != null){
                for(Stamps stamp:stampRally.getStampsCollection()){
                    stampBean = new StampBean();
                    stampBean.setStampTitle(stamp.getStampName());
                    stampList.add(stampBean);
                }
                adapter.notifyDataSetChanged(); //リストの更新
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
