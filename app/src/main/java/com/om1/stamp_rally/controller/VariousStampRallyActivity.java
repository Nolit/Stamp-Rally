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
import com.om1.stamp_rally.model.VariousStampRallyModel;
import com.om1.stamp_rally.model.adapter.ResultSearchListAdapter;
import com.om1.stamp_rally.model.bean.StampRallyBean;
import com.om1.stamp_rally.model.event.FetchedJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import data.StampRallyData;

public class VariousStampRallyActivity extends AppCompatActivity {
    private final EventBus eventBus = EventBus.getDefault();
    private StampRallyData[] stampRallyDatas;

    ListView lv;
    StampRallyBean stampRallyBean;
    ArrayList<StampRallyBean> stampRallyList = new ArrayList<>();
    ResultSearchListAdapter adapter;

    private TextView showStampRally;    //スタンプラリーの表示形式
    private TextView noHitResult;       //表示件数が0件の時表示される

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_various_stamprally);
        lv = (ListView) findViewById(R.id.StampRallyList);

        VariousStampRallyModel.getInstance().fetchJson(
                getIntent().getStringExtra("referenceUserId"),
                getIntent().getStringExtra("mode"));

        showStampRally = (TextView) findViewById(R.id.showStampRally);
        showStampRally.setText(getIntent().getStringExtra("showStampRally"));
        noHitResult = (TextView) findViewById(R.id.NoHitResult);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listView = (ListView) parent;
                StampRallyBean stampRallyBean = (StampRallyBean) listView.getItemAtPosition(position);
                Intent intent = new Intent(VariousStampRallyActivity.this, StampRallyDetailActivity.class);

                if(getIntent().getStringExtra("mode").equals("complete")){
                    intent.putExtra("referenceUserId", getIntent().getStringExtra("referenceUserId"));
                }else{
                    intent.putExtra("referenceUserId", String.valueOf(stampRallyBean.getCreatorUserId()));
                }

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
            stampRallyDatas = new ObjectMapper().readValue(event.getJson(), StampRallyData[].class);
            if(stampRallyDatas.length < 1){
                noHitResult.setText("データがありません");
            }else{
                stampRallyList = new ArrayList<StampRallyBean>();
                adapter = new ResultSearchListAdapter(this, 0, stampRallyList);

                for( StampRallyData stampRally : stampRallyDatas ){
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
