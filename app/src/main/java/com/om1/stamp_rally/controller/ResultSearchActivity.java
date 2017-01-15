package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.SearchStampRallyModel;
import com.om1.stamp_rally.model.adapter.ResultSearchListAdapter;
import com.om1.stamp_rally.model.bean.StampRallyBean;
import com.om1.stamp_rally.model.event.FetchJsonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class ResultSearchActivity extends AppCompatActivity {

    ListView lv;
    StampRallyBean stampRallyBean;
    ArrayList<StampRallyBean> stampRallyList = new ArrayList<StampRallyBean>();
    ResultSearchListAdapter adapter;

    private final EventBus eventBus = EventBus.getDefault();
    private List<LinkedHashMap<String, Object>> stampRallys;

    private TextView searchKeyword;
    private TextView noHitResult;       //検索結果が0件の場合表示されるテキストビュー

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
        noHitResult = (TextView) findViewById(R.id.NoHitResult);

        stampRallyBean = new StampRallyBean();
        stampRallyList = new ArrayList<StampRallyBean>();
        adapter = new ResultSearchListAdapter(getApplication());
        adapter.setStampRallyList(stampRallyList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listView = (ListView) parent;
                StampRallyBean stampRallyBean = (StampRallyBean) listView.getItemAtPosition(position);
                Intent intent = new Intent(ResultSearchActivity.this, StampRallyDetailActivity.class);
                intent.putExtra("stampRallyId", String.valueOf(stampRallyBean.getStampRallyId()));
                startActivity(intent);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:ResultSearch", "データベースとの通信に失敗");
            noHitResult.setText("データベース接続に失敗しました");
            return;
        }
//        try {
//            Log.d("デバッグ:ResultSearch", "データベースとの通信に成功");
//            String[] responseData = event.getJson().split(System.getProperty("line.separator"));
//
//            if(stampRallys.size() < 1){
//                noHitResult.setText("検索結果がありませんでした。");
//            }else{
//
//                /* stampRallyBeanを作成していく処理を追加する */
////                for(){
////                    stampRallyBean = new StampRallyBean();
////                    stampRallyBean.setStampRallyId();
////                    stampRallyBean.setPictPath();
////                    stampRallyBean.setStampRallyTitle();
////                    stampRallyBean.setCreatorName();
////                }
//
//                adapter.notifyDataSetChanged();
//            }
//
//        }catch(IOException e){
//            e.printStackTrace();
//        }
    }

}
