package com.om1.stamp_rally.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.MyStampBookModel;
import com.om1.stamp_rally.model.adapter.MyStampBookListAdapter;
import com.om1.stamp_rally.model.adapter.StructureStampListAdapter;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.dbadapter.StructureStampDbAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import data.StampData;

public class SpecifyStampRallyStructure extends AppCompatActivity {
    private StampData[] myStampBook;
    private StructureStampListAdapter adapter;
    private Integer stampRallyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_stamp_rally_structure);
        ButterKnife.inject(this);

        MyStampBookModel.getInstance().fetchJson(getSharedPreferences("main", MODE_PRIVATE).getString("loginUserId", "20"));
        stampRallyId = getIntent().getIntExtra("stampRallyId", -1);
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBusUtil.defaultBus.register(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        EventBusUtil.defaultBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:MyStampBook", "データベースとの通信に失敗");
            return;
        }
        try {
            Log.d("デバッグ:MyStampBook", "データベースとの通信に成功");
            String[] responseData = event.getJson().split(System.getProperty("line.separator"));
            myStampBook = new ObjectMapper().readValue(responseData[1], StampData[].class);
            ArrayList<StampBean> myStampList = new ArrayList<>();
            for(StampData stampData : myStampBook){
                StampBean stampBean = new StampBean();
                stampBean.setStampId(stampData.getStampId());
                stampBean.setPictPath(stampData.getPicture());
                stampBean.setStampTitle(stampData.getStampName());
                stampBean.setStampDate(stampData.getStampDate());
                myStampList.add(stampBean);
            }
            adapter = new StructureStampListAdapter(this, 0, myStampList);
            adapter.setSelectedStampIds(new StructureStampDbAdapter(this).getByStampRallyIdAsList(stampRallyId));
            ListView lv = (ListView) findViewById(R.id.structureStampList);
            lv.setAdapter(adapter);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.saveStructureStampButton)
    public void saveStructureStamp(){
        StructureStampDbAdapter dbAdapter = new StructureStampDbAdapter(this);
        dbAdapter.truncate();

        for(Integer stampId : adapter.getSelectedStampId()){
            dbAdapter.createRelation(stampId, stampRallyId);
        }
        dbAdapter.log();
        Toast.makeText(this, "スタンプを登録しました", Toast.LENGTH_SHORT);
        finish();
    }
}
