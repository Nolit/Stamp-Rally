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
import com.om1.stamp_rally.model.UserListModel;
import com.om1.stamp_rally.model.adapter.UserListAdapter;
import com.om1.stamp_rally.model.bean.UserBean;
import com.om1.stamp_rally.model.event.FetchedJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import data.UserData;

public class FollowActivity extends AppCompatActivity {
    private final EventBus eventBus = EventBus.getDefault();
    private UserData userDatas[];

    ListView lv;
    UserBean userBean;
    ArrayList<UserBean> userList = new ArrayList<>();
    UserListAdapter adapter;

    private TextView showFollow;        //最上部に表示（〜さんのフォロワー）
    private TextView noHitResult;       //表示件数が0件の時表示される

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        lv = (ListView) findViewById(R.id.FollowUserList);

        UserListModel.getInstance().fetchJson(getIntent().getStringExtra("referenceUserId"), getIntent().getStringExtra("mode"));

        showFollow = (TextView) findViewById(R.id.showUserList);
        showFollow.setText(getIntent().getStringExtra("showFollow"));
        noHitResult = (TextView) findViewById(R.id.NoHitResult);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listView = (ListView) parent;
                UserBean userBean = (UserBean) listView.getItemAtPosition(position);
                Intent intent = new Intent(FollowActivity.this, MyPageOtherActivity.class);
                intent.putExtra("referenceUserId", String.valueOf(userBean.getUserId()));
                startActivity(intent);
            }
        });

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchedJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:ResultSearch", "データベースとの通信に失敗");
            noHitResult.setText("データベース接続に失敗しました");
            return;
        }
        try {
            Log.d("デバッグ:FollowActivity", "データベースとの通信に成功");
            String[] responseData = event.getJson().split(System.getProperty("line.separator"));
            userDatas = new ObjectMapper().readValue(responseData[0], UserData[].class);
            if(userDatas.length < 1){
                noHitResult.setText("データがありません");
            }else{
                userList = new ArrayList<UserBean>();
                adapter = new UserListAdapter(this, 0, userList);

                for( UserData userData : userDatas ){
                    userBean = new UserBean();
                    userBean.setPictPath(userData.getPicture());
                    userBean.setUserId(userData.getUserId());
                    userBean.setUserName(userData.getUserName());
                    userBean.setSearchId(userData.getSearchId());
                    userList.add(userBean);
                }
                adapter.setUserList(userList);
                adapter.notifyDataSetChanged();
                lv.setAdapter(adapter);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
