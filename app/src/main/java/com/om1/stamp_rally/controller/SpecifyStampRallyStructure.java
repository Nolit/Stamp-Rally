package com.om1.stamp_rally.controller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.MyStampBookModel;
import com.om1.stamp_rally.model.adapter.MyStampListAdapter;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class SpecifyStampRallyStructure extends AppCompatActivity {
    private Map<String, Object> myStampBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_stamp_rally_structure);
        ButterKnife.inject(this);

        MyStampBookModel.getInstance().fetchJson(getSharedPreferences("main", MODE_PRIVATE).getString("loginUserId", "20"));
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
            ObjectMapper mapper = new ObjectMapper();
            myStampBook = mapper.readValue(event.getJson(), Map.class);

            List<Map<String, Object>> haveStampList = (List<Map<String, Object>>) myStampBook.get("Stamps");
            ArrayList<StampBean> myStampList = new ArrayList<>();

            for(Map<String, Object> stampData : haveStampList){
                StampBean stampBean = new StampBean();
                stampBean.setPictPath(Base64.decode((String) stampData.get("picture"), Base64.DEFAULT));
                stampBean.setStampTitle((String) stampData.get("stampName"));
                stampBean.setStampDate((String) stampData.get("stampDate"));
                stampBean.setStampRallyName((String) stampData.get("stampRallyName"));
                myStampList.add(stampBean);
            }
            MyStampListAdapter adapter = new MyStampListAdapter(this, 0, myStampList);
            //TODO ここは使用しているレイアウトによって変わる
            ListView lv = (ListView) findViewById(R.id.structireStampList);
            lv.setAdapter(adapter);
        }catch(IOException e){
            e.printStackTrace();

        }
    }
}
