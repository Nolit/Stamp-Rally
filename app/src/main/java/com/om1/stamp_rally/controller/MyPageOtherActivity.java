package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.MyPageModel;
import com.om1.stamp_rally.model.event.FetchedJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import database.entities.Users;

public class MyPageOtherActivity extends AppCompatActivity {
    private static final String FOLLOW_ON = "フォローしました";
    SharedPreferences mainPref;
    private final EventBus eventBus = EventBus.getDefault();

    @InjectView(R.id.profileThumbnail)
    ImageView profileThumbnail;
    @InjectView(R.id.userName)
    TextView userName;
    @InjectView(R.id.profile)
    TextView profile;
    @InjectView(R.id.settings_and_follow_button)
    ImageButton settingsAndFollowButton;
    @InjectView(R.id.follow)
    Button followNum;
    @InjectView(R.id.follower)
    Button followerNum;

    boolean followStatus;   //true（フォロー中）・false（未フォロー）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_other);
        ButterKnife.inject(this);
        mainPref = getSharedPreferences("main", MODE_PRIVATE);

        //設定兼フォローボタンの設定
        if(getIntent().getStringExtra("referenceUserId") != null){
            if(!mainPref.getString("loginUserId", null).equals(getIntent().getStringExtra("referenceUserId"))){
                settingsAndFollowButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_follow_off));
            }
        }else{
            System.out.println("デバッグ:MyPageOther:IntentからreferenceUserIdが取得できません");
            return;
        }

        MyPageModel.getInstance().myPageOther(
                mainPref.getString("mailAddress", null),
                mainPref.getString("password", null),
                getIntent().getStringExtra("referenceUserId")
        );

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
            Log.d("デバッグ:MainActivity", "データベースとの通信に失敗");
            Toast.makeText(MyPageOtherActivity.this, "データベースとの通信に失敗しました", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Log.d("デバッグ:MainActivity", "データベースとの通信に成功");
            Users loginUser = new ObjectMapper().readValue(event.getJson(), Users.class);
            if (loginUser.getThumbnailData() != null) {
                byte[] notDecodedThumbnail = loginUser.getThumbnailData();
                Bitmap thumbnail = BitmapFactory.decodeByteArray(notDecodedThumbnail, 0, notDecodedThumbnail.length);
                profileThumbnail.setImageBitmap(thumbnail);        //プロフィール画像
            } else {
                profileThumbnail.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.default_image_view));
            }
            userName.setText(loginUser.getUserName());
            profile.setText(loginUser.getProfile());

            //フォローボタンの設定
//            followStatus = ;
            if(followStatus){
                //フォロー中
                settingsAndFollowButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_follow_on));
            }else{
                //未フォロー
                settingsAndFollowButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_follow_off));
            }

//            followNum.setText();
//            followerNum.setText();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.settings_and_follow_button)
    void clickSettingsAndFollowButton(){
        if(mainPref.getString("loginUserId", null).equals(getIntent().getStringExtra("referenceUserId"))){
            startActivity(new Intent(MyPageOtherActivity.this, SettingsActivity.class));
            return;
        }

        if(followStatus){
            //フォロー解除
            settingsAndFollowButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_follow_off));
            followStatus = !followStatus;
            MyPageModel.getInstance().followRequest(
                    mainPref.getString("mailAddress", null),
                    mainPref.getString("password", null),
                    getIntent().getStringExtra("referenceUserId"),
                    followStatus);
        }else{
            //フォロー申請
            settingsAndFollowButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_follow_on));
            Toast.makeText(this, FOLLOW_ON, Toast.LENGTH_SHORT).show();
            followStatus = !followStatus;
            MyPageModel.getInstance().followRequest(
                    mainPref.getString("mailAddress", null),
                    mainPref.getString("password", null),
                    getIntent().getStringExtra("referenceUserId"),
                    followStatus);
        }
    }

    @OnClick(R.id.myStampBookIntentButton)
    void cilckMyStampBookIntentButton(){
        Intent intent = new Intent(MyPageOtherActivity.this, MyStampBookActivity.class);
        intent.putExtra("referenceUserId", getIntent().getStringExtra("referenceUserId"));
        startActivity(intent);
    }

    @OnClick(R.id.completeStampRallyIntentButton)
    void clickCompleteStampRallyIntentButton(){
        Intent intent = new Intent(MyPageOtherActivity.this, VariousStampRallyActivity.class);
        intent.putExtra("referenceUserId", getIntent().getStringExtra("referenceUserId"));
        intent.putExtra("showStampRally", userName.getText() + "さんのクリアスタンプラリー");
        intent.putExtra("mode", "complete");
        startActivity(intent);
    }

    @OnClick(R.id.createStampRallyIntentButton)
    void clickCreateStampRallyIntentButton(){
        Intent intent = new Intent(MyPageOtherActivity.this, VariousStampRallyActivity.class);
        intent.putExtra("referenceUserId", getIntent().getStringExtra("referenceUserId"));
        intent.putExtra("showStampRally", userName.getText() + "さんの作成スタンプラリー");
        intent.putExtra("mode", "create");
        startActivity(intent);
    }

    @OnClick(R.id.favoriteStampRallyIntentButton)
    void clickFavoriteStampRallyIntentButton(){
        Intent intent = new Intent(MyPageOtherActivity.this, VariousStampRallyActivity.class);
        intent.putExtra("referenceUserId", getIntent().getStringExtra("referenceUserId"));
        intent.putExtra("showStampRally", userName.getText() + "さんのお気に入りスタンプラリー");
        intent.putExtra("mode", "favorite");
        startActivity(intent);
    }

    @OnClick(R.id.follow)
    void clickFollow(){
        Intent intent = new Intent(MyPageOtherActivity.this, FollowActivity.class);
        intent.putExtra("referenceUserId", getIntent().getStringExtra("referenceUserId"));
        intent.putExtra("showFollow", userName.getText() + "さんのフォロー");
        startActivity(intent);
    }

    @OnClick(R.id.follower)
    void clickFollower(){
        Intent intent = new Intent(MyPageOtherActivity.this, FollowActivity.class);
        intent.putExtra("referenceUserId", getIntent().getStringExtra("referenceUserId"));
        intent.putExtra("showFollow", userName.getText() + "さんのフォロワー");
        startActivity(intent);
    }

}
