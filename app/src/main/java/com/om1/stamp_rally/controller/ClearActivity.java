package com.om1.stamp_rally.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampRallyClear;
import com.om1.stamp_rally.utility.dbadapter.StampDbAdapter;
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClearActivity extends AppCompatActivity {
    private final EventBus eventBus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        ButterKnife.inject(this);
        eventBus.register(this);
    }

    @Override
    public void onStop() {
        //EventBusライブラリによる自身の登録解除
        eventBus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.laterButton)
    void laterUpload(){

    }

    @OnClick(R.id.uploadButton)
    void upload(){
//        int id = (Integer) new StampRallyDbAdapter(this).getTryingStampRally().get(StampRallyDbAdapter.ID);
//        List<Map<String, Object>> stampList =  new StampDbAdapter(this).getListByStampRallyId(id);
//        try {
//            StampRallyClear.getInstance().upload(stampList);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
    }


}
