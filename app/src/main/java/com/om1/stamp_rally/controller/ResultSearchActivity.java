package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.SearchStampRallyModel;
import com.om1.stamp_rally.model.adapter.ResultSearchListAdapter;
import com.om1.stamp_rally.model.bean.StampRallyBean;
import com.om1.stamp_rally.model.event.FetchedJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.Overlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import data.StampRallyData;

import static butterknife.ButterKnife.findById;


public class ResultSearchActivity extends AppCompatActivity {
    private StampRallyData[] searchData;

    ListView lv;
    StampRallyBean stampRallyBean;
    ArrayList<StampRallyBean> stampRallyList = new ArrayList<>();
    ResultSearchListAdapter adapter;

    private TextView searchKeyword;
    private TextView noHitResult;       //検索結果が0件の場合表示されるテキストビュー
    private Overlayer overlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_search);
        lv = (ListView) findViewById(R.id.ResultSearchList);
        overlayer = new Overlayer(this);
        overlayer.showProgress();
        SearchStampRallyModel searchStampRallyModel = SearchStampRallyModel.getInstance();
        searchStampRallyModel.searchStampRally(getIntent().getStringExtra("searchKeyword"));    //通信開始

        searchKeyword = (TextView) findViewById(R.id.SearchKeyword);
        searchKeyword.setText(getIntent().getStringExtra("searchKeyword"));
        noHitResult = (TextView) findViewById(R.id.NoHitResult);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listView = (ListView) parent;
                StampRallyBean stampRallyBean = (StampRallyBean) listView.getItemAtPosition(position);
                Intent intent = new Intent(ResultSearchActivity.this, StampRallyDetailActivity.class);
                intent.putExtra("referenceUserId", String.valueOf(stampRallyBean.getCreatorUserId()));
                intent.putExtra("stampRallyId", String.valueOf(stampRallyBean.getStampRallyId()));
                startActivity(intent);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchedJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:ResultSearch", "データベースとの通信に失敗");
            noHitResult.setText("データベース接続に失敗しました");
            return;
        }
        try {
            Log.d("デバッグ:ResultSearch", "データベースとの通信に成功");
            String[] responseData = event.getJson().split(System.getProperty("line.separator"));
            searchData = new ObjectMapper().readValue(responseData[0], StampRallyData[].class);
            if(searchData.length < 1){
                noHitResult.setText("検索結果がありませんでした。");
            }else{
                stampRallyList = new ArrayList<StampRallyBean>();
                adapter = new ResultSearchListAdapter(this, 0, stampRallyList);

                for( StampRallyData stampRally : searchData ){
                    stampRallyBean = new StampRallyBean();
                    stampRallyBean.setStampRallyId( stampRally.getStampRallyId() );
                    stampRallyBean.setPictPath( stampRally.getPicture() );
                    stampRallyBean.setStampRallyTitle( stampRally.getStampRallyTitle() );
                    stampRallyBean.setCreatorUserName( stampRally.getStampRallyCreatorName() );
                    stampRallyBean.setCreatorUserId( stampRally.getStampRallyCreatorUserId() );
                    stampRallyList.add(stampRallyBean);
                }
                adapter.setStampRallyList(stampRallyList);
                adapter.notifyDataSetChanged();
                lv.setAdapter(adapter);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        overlayer.hideProgress();
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBusUtil.defaultBus.register(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        EventBusUtil.defaultBus.unregister(this);
    }
}
