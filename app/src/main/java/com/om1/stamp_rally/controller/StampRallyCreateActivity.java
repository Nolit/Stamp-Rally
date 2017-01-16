package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.CreatedStampRallyUploader;
import com.om1.stamp_rally.model.MyStampBookModel;
import com.om1.stamp_rally.model.adapter.MyStampBookListAdapter;
import com.om1.stamp_rally.model.adapter.StructureStampListAdapter;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;
import com.om1.stamp_rally.utility.dbadapter.StructureStampDbAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import data.StampData;

import static butterknife.ButterKnife.findById;

public class StampRallyCreateActivity extends AppCompatActivity {
    EditText editTitle;
    EditText editComment;
    Map<String, Object> stampRallyData;

    SharedPreferences mainPref;
    Integer stampRallyId;
    private final float OVERLAY_ALPHA = 0.7f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_create);
        ButterKnife.inject(this);
        mainPref = getSharedPreferences("main", MODE_PRIVATE);
        stampRallyId = getIntent().getIntExtra("stampRallyId", -1);

        editTitle = (EditText) findViewById(R.id.editStampRallyTitle);
        editComment = (EditText) findViewById(R.id.editStampRallyComment);
        stampRallyData = new StampRallyDbAdapter(this).getById(stampRallyId);
        editTitle.setText((String) stampRallyData.get("name"));
        editComment.setText((String) stampRallyData.get("summary"));

        MyStampBookModel.getInstance().fetchJson(mainPref.getString("loginUserId", "20"));
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
            StampData[] myStampBook = new ObjectMapper().readValue(responseData[1], StampData[].class);
            ArrayList<StampBean> myStampList = new ArrayList<>();
            List<Integer> idList = new StructureStampDbAdapter(this).getByStampRallyIdAsList(stampRallyId);
            for(StampData stampData : myStampBook){
                for(Integer id : idList){
                    if(id.equals(stampData.getStampId())){
                        StampBean stampBean = new StampBean();
                        stampBean.setStampId(stampData.getStampId());
                        stampBean.setPictPath(stampData.getPicture());
                        stampBean.setStampTitle(stampData.getStampName());
                        myStampList.add(stampBean);
                        break;
                    }
                }
            }
            MyStampBookListAdapter adapter = new MyStampBookListAdapter(this, 0, myStampList);
            ListView lv = (ListView) findViewById(R.id.stampInsertedListView);
            lv.setAdapter(adapter);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.saveStampRallyButton)
    public void saveStampRally(){
        stampRallyData.put("name", editTitle.getText().toString());
        stampRallyData.put("summary", editComment.getText().toString());
        new StampRallyDbAdapter(this).update(stampRallyData);
        finish();
    }

    @OnClick(R.id.addStampButton)
    public void addStamp(){
        Intent i = new Intent(StampRallyCreateActivity.this, SpecifyStampRallyStructure.class);
        i.putExtra("stampRallyId", getIntent().getIntExtra("stampRallyId", -1));
        startActivity(i);
    }

    @OnClick(R.id.uploadButton)
    public void upload(){
        showOverlay();
        String userId = mainPref.getString("loginUserId", "20");
        String title = editTitle.getText().toString();
        String summary = editComment.getText().toString();
        List<Integer> selectedStampIdList = new StructureStampDbAdapter(this).getByStampRallyIdAsList(stampRallyId);
        Integer thumbnailStampId = selectedStampIdList.size() > 0 ? selectedStampIdList.get(0) : 1;
        try {
            CreatedStampRallyUploader.getInstance().postStampRally(userId, title, summary, selectedStampIdList, thumbnailStampId);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadedStampRally(Boolean isSuccess) {
        if (!isSuccess) {
            Log.d("デバッグ:MyStampBook", "データベースとの通信に失敗");
            Toast.makeText(this, "アップロードに失敗しました。\nしばらく待ってお試しください。", Toast.LENGTH_SHORT).show();
            saveStampRally();
            return;
        }

        Log.d("デバッグ:MyStampBook", "データベースとの通信に成功");
        Toast.makeText(this, "アップロードしました", Toast.LENGTH_SHORT).show();
        new StampRallyDbAdapter(this).deleteById(stampRallyId);
        new StructureStampDbAdapter(this).deleteBySrampRallyId(stampRallyId);
        startActivity(new Intent(this, StampRallyControlActivity.class));
    }

    private void showOverlay(){
        FrameLayout overlayLayout = findById(this, R.id.uploading_overlay);
        overlayLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        overlayLayout.setAlpha(OVERLAY_ALPHA);
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
}
