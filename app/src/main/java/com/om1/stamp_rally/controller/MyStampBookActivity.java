package com.om1.stamp_rally.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.MyStampBookModel;
import com.om1.stamp_rally.model.adapter.MyStampListAdapter;
import com.om1.stamp_rally.model.adapter.ResultSearchListAdapter;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.event.FetchJsonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import database.entities.StampRallys;
import database.entities.Stamps;

public class MyStampBookActivity extends AppCompatActivity {
    SharedPreferences mainPref;
    private final EventBus eventBus = EventBus.getDefault();
    private StampRallys stampRally;

    ListView lv;
    StampBean stampBean;
    ArrayList<StampBean> mayStampList = new ArrayList<>();
    MyStampListAdapter adapter;

    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystamp_book);
        eventBus.register(this);
        lv = (ListView) findViewById(R.id.MyStampList);
        MyStampBookModel model = MyStampBookModel.getInstance();

        if(getIntent().getStringExtra("referenceUserId") != null){
            model.fetchJson(getIntent().getStringExtra("referenceUserId"));    //通信開始
        }else{
            System.out.println("デバッグ:MyStampBook:getStringExtraが"+getIntent().getStringExtra("referenceUserId")+"です。");
        }

        userName = (TextView) findViewById(R.id.UserName);
//        userName.setText();

    }

    //データベース通信処理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:MyStampBook", "データベースとの通信に失敗");
            return;
        }
        try {
            stampRally = new ObjectMapper().readValue(event.getJson(), StampRallys.class);
            Log.d("デバッグ:MyStampBook", "データベースとの通信に成功");

            /* 処理を追加する　*/


        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
