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
import com.om1.stamp_rally.model.MyStampBookModel;
import com.om1.stamp_rally.model.adapter.MyStampBookListAdapter;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import data.StampData;

public class MyStampBookActivity extends AppCompatActivity {
    private final EventBus eventBus = EventBus.getDefault();
    private StampData[] myStampBook;

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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listView = (ListView) parent;
                StampBean stampBean = (StampBean) listView.getItemAtPosition(position);
                Intent intent = new Intent(MyStampBookActivity.this, StampDetailActivity.class);
                intent.putExtra("stampId", String.valueOf(stampBean.getStampId()));
                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:MyStampBook", "データベースとの通信に失敗");
            notHaveStamp.setText("データベース接続に失敗しました");
            return;
        }
        try {
            Log.d("デバッグ:MyStampBook", "データベースとの通信に成功");
            String[] responseData = event.getJson().split(System.getProperty("line.separator"));
            userName.setText(responseData[0]);
            myStampBook = new ObjectMapper().readValue(responseData[1], StampData[].class);

            if(myStampBook.length < 1){
                System.out.println("デバッグ:MyStampBook:所持しているスタンプはありません。");
                notHaveStamp.setText("スタンプを所持していません");
            }else{
                for(StampData stampData : myStampBook){
                    stampBean = new StampBean();
                    stampBean.setStampId(stampData.getStampId());
                    stampBean.setPictPath(stampData.getPicture());
                    stampBean.setStampTitle(stampData.getStampName());
                    stampBean.setStampDate(stampData.getStampDate());
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
