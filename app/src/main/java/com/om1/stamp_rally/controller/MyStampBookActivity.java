package com.om1.stamp_rally.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.MyStampBookModel;
import com.om1.stamp_rally.model.adapter.MyStampBookListAdapter;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.event.FetchJsonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import database.entities.StampRallys;

public class MyStampBookActivity extends AppCompatActivity {
    SharedPreferences mainPref;
    private final EventBus eventBus = EventBus.getDefault();
    private Map<String, Object> myStampBook;
    private StampRallys stampRally;

    ListView lv;
    StampBean stampBean = new StampBean();
    ArrayList<StampBean> myStampList = new ArrayList<>();
    MyStampBookListAdapter adapter;

    private TextView userName;
    private TextView notHaveStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystamp_book);
        eventBus.register(this);
        lv = (ListView) findViewById(R.id.MyStampList);
        MyStampBookModel model = MyStampBookModel.getInstance();

        if(getIntent().getStringExtra("referenceUserId") != null){
            model.fetchJson(getIntent().getStringExtra("referenceUserId"));
        }else{
            System.out.println("デバッグ:MyStampBook:getStringExtraが"+getIntent().getStringExtra("referenceUserId")+"です。");
        }

        userName = (TextView) findViewById(R.id.UserName);
        notHaveStamp = (TextView) findViewById(R.id.NotHaveStamp);  //所持スタンプ数が0の場合に表示されるテキスト

    }

    //データベース通信処理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:MyStampBook", "データベースとの通信に失敗");
            notHaveStamp.setText("データベース接続に失敗しました");
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            myStampBook = mapper.readValue(event.getJson(), Map.class);
            Log.d("デバッグ:MyStampBook", "データベースとの通信に成功");

            userName.setText((String) myStampBook.get("userName"));

            List<Map<String, Object>> haveStampList = (List<Map<String, Object>>) myStampBook.get("Stamps");

            if(haveStampList.size() < 1){
                System.out.println("デバッグ:MyStampBook:所持しているスタンプはありません。");
                notHaveStamp.setText("スタンプを所持していません");
            }else{
                for(Map<String, Object> stampData : haveStampList){
                    stampBean = new StampBean();
                    stampBean.setPictPath(Base64.decode((String) stampData.get("picture"),Base64.DEFAULT));
                    stampBean.setStampTitle((String) stampData.get("stampName"));
                    stampBean.setStampDate((String) stampData.get("stampDate"));
                    myStampList.add(stampBean);
                }
                adapter = new MyStampBookListAdapter(this, 0, myStampList);
                adapter.notifyDataSetChanged();
                lv.setAdapter(adapter);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
