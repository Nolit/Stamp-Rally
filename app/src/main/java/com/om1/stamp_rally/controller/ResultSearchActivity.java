package com.om1.stamp_rally.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.SearchStampRallyModel;
import com.om1.stamp_rally.model.StampRallyDetailModel;
import com.om1.stamp_rally.model.adapter.ResultSearchListAdapter;
import com.om1.stamp_rally.model.bean.StampRallyBean;
import com.om1.stamp_rally.model.event.FetchJsonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import database.entities.StampRallys;
import database.entities.Stamps;

public class ResultSearchActivity extends AppCompatActivity {

    ListView lv;
    StampRallyBean stampRallyBean;
    ArrayList<StampRallyBean> stampRallyList = new ArrayList<StampRallyBean>();
    ResultSearchListAdapter adapter;

    private final EventBus eventBus = EventBus.getDefault();
    private StampRallys stampRally;

    private TextView searchKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_search);
        eventBus.register(this);
        lv = (ListView) findViewById(R.id.ResultSearchList);
        SearchStampRallyModel searchStampRallyModel = SearchStampRallyModel.getInstance();
        searchStampRallyModel.searchStampRally(getIntent().getStringExtra("searchKeyword"));    //通信開始

        searchKeyword = (TextView) findViewById(R.id.SearchKeyword);
        searchKeyword.setText(getIntent().getStringExtra("searchKeyword"));

        stampRallyBean = new StampRallyBean();
        stampRallyList = new ArrayList<StampRallyBean>();
        adapter = new ResultSearchListAdapter(getApplication());
        adapter.setStampRallyList(stampRallyList);
        lv.setAdapter(adapter);

        //リスト項目がクリックされた時の処理
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), item + " clicked",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //リスト項目が選択された時の処理
        lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), item + " selected",
                        Toast.LENGTH_SHORT).show();
            }
            //リスト項目がなにも選択されていない時の処理
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "no item selected",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    //データベース通信処理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ", "データベースとの通信に失敗");
            return;
        }
        try {
            //Jsonをオブジェクトに変換
            stampRally = new ObjectMapper().readValue(event.getJson(), StampRallys.class);
            Log.d("デバッグ", "データベースとの通信に成功");
            for (Stamps stamps : stampRally.getStampList()) {
                System.out.println("デバッグ:詳細ページ:" + stamps.getStampName());
            }
        }catch(IOException e){
                e.printStackTrace();
        }
    }

}