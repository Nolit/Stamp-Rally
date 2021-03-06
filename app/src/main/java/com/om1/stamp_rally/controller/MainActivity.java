package com.om1.stamp_rally.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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

public class MainActivity  extends FragmentActivity implements OnMapReadyCallback {
    private final String FOLLOW_UNIT = "人";
    SharedPreferences mainPref;             //ログアウト時にPreferencesは削除する
    Bundle savedInstanceState;

    //tabHost
    @InjectView(R.id.tabHost)
    TabHost th;
    //--トップ
    @InjectView(R.id.SearchEdit)
    EditText search;
    //--マイページ
    @InjectView(R.id.profileThumbnail)
    ImageView profileThumbnail;
    @InjectView(R.id.userName)
    TextView userName;
    @InjectView(R.id.follow)
    TextView followButton;
    @InjectView(R.id.follower)
    TextView followerButton;
    @InjectView(R.id.profile)
    TextView profile;
    //--マップ
    FragmentManager mapFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPref = getSharedPreferences("main", MODE_PRIVATE);

        //ログイン確認
        if(mainPref.getString("mailAddress",null) != null && mainPref.getString("password", null) != null){
            generateTabs();
        }else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        ButterKnife.inject(this);
        mapFragmentManager = getSupportFragmentManager();
        this.savedInstanceState = savedInstanceState;

        //検索バーによるキーボード表示を無効化
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        //タブのリスナー
        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("PLAY") && mapFragmentManager.findFragmentById(R.id.StampRally) == null){
                    //スタンプラリーページを選択時のフラグメント起動
                    mapFragmentManager.beginTransaction().add(R.id.StampRally, new MapsFragment()).commit();

                }else if(tabId.equals("HOME")){
                    //マイページタブ選択時にユーザー情報を取得する
                    MyPageModel myPageModel = MyPageModel.getInstance();
                    myPageModel.fetchJson(mainPref.getString("mailAddress",null), mainPref.getString("password", null));
                }
            }
        });

    }

    //タブホスト生成
    protected void generateTabs() {
        try {
            TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
            tabHost.setup();
            TabHost.TabSpec spec;
            // TOPページタブ
            spec = tabHost.newTabSpec("TOP")
                    .setIndicator("TOP", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.TOP);
            tabHost.addTab(spec);
            // マイページタブ
            spec = tabHost.newTabSpec("HOME")
                    .setIndicator("HOME", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.MyPage);
            tabHost.addTab(spec);
            // スタンプラリータブ
            spec = tabHost.newTabSpec("PLAY")
                    .setIndicator("Play", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.StampRally);
            tabHost.addTab(spec);
            // スタンプ登録タブ
            spec = tabHost.newTabSpec("スタンプ")
                    .setIndicator("STAMP", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.StampRegistration);
            tabHost.addTab(spec);
            tabHost.setCurrentTab(0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("map", "ready");
    }

    @Override
    public void onStart(){
        super.onStart();
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

    //トップ
    @OnClick(R.id.OfficialStampRally)                       //公式スタンプラリー
    void clickOfficilaStampRally(){
//        Intent intent = new Intent(MainActivity.this, StampRallyDetailActivity.class);
//        intent.putExtra("stampRallyId", );←ここ指定
//        intent.putExtra("referenceUserId", );←ここ指定
//        startActivity(intent);
    }
    @OnClick(R.id.IntroductionStampRally1)                  //紹介スタンプラリー1
    void clickIntroductionStampRally1(){
//        Intent intent = new Intent(MainActivity.this, StampRallyDetailActivity.class);
//        intent.putExtra("stampRallyId", );←ここ指定
//        intent.putExtra("referenceUserId", );←ここ指定
//        startActivity(intent);
    }
    @OnClick(R.id.IntroductionStampRally2)                  //紹介スタンプラリー2
    void clickIntroductionStampRally2(){
//        Intent intent = new Intent(MainActivity.this, StampRallyDetailActivity.class);
//        intent.putExtra("stampRallyId", );←ここ指定
//        intent.putExtra("referenceUserId", );←ここ指定
//        startActivity(intent);
    }
    @OnClick(R.id.IntroductionStampRally3)                  //紹介スタンプラリー3
    void clickIntroductionStampRally3(){
//        Intent intent = new Intent(MainActivity.this, StampRallyDetailActivity.class);
//        intent.putExtra("stampRallyId", );←ここ指定
//        intent.putExtra("referenceUserId", );←ここ指定
//        startActivity(intent);
    }
    @OnClick(R.id.SearchBt)                                 //--検索ボタン
    void search() {
        Intent intent = new Intent(MainActivity.this, ResultSearchActivity.class);
        intent.putExtra("searchKeyword", search.getText().toString());
        startActivity(intent);
    }
    //マイページ
    @OnClick(R.id.settings_and_follow_button)               //--設定ボタン兼フォローボタン
    void clickSettingsButton(){
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }
    @OnClick(R.id.myStampBookIntentButton)                  //--マイスタンプ帳
    void clickMyStampBookIntentButton(){
        Intent intent = new Intent(MainActivity.this, MyStampBookActivity.class);
        intent.putExtra("referenceUserId", mainPref.getString("loginUserId", null));
        startActivity(intent);
    }
    @OnClick(R.id.completeStampRallyIntentButton)           //--クリアスタンプラリー
    void clickCompleteStampRallyIntentButton(){
        Intent intent = new Intent(MainActivity.this, VariousStampRallyActivity.class);
        intent.putExtra("referenceUserId", mainPref.getString("loginUserId", null));
        intent.putExtra("showStampRally", userName.getText() + "さんのクリアスタンプラリー");
        intent.putExtra("mode", "complete");
        startActivity(intent);
    }
    @OnClick(R.id.createStampRallyIntentButton)             //作成スタンプラリー
    void clickCreateStampRallyIntentButton(){
        Intent intent = new Intent(MainActivity.this, VariousStampRallyActivity.class);
        intent.putExtra("referenceUserId", mainPref.getString("loginUserId", null));
        intent.putExtra("showStampRally", userName.getText() + "さんの作成スタンプラリー");
        intent.putExtra("mode", "create");
        startActivity(intent);
    }
    @OnClick(R.id.favoriteStampRallyIntentButton)           //お気に入りスタンプラリー
    void clickFavoriteStampRallyIntentButton(){
        Intent intent = new Intent(MainActivity.this, VariousStampRallyActivity.class);
        intent.putExtra("referenceUserId", mainPref.getString("loginUserId", null));
        intent.putExtra("showStampRally", userName.getText() + "さんのお気に入りスタンプラリー");
        intent.putExtra("mode", "favorite");
        startActivity(intent);
    }
    @OnClick(R.id.follow)           //フォロー
    void clickFollow(){
        Intent intent = new Intent(MainActivity.this, FollowActivity.class);
        intent.putExtra("referenceUserId", mainPref.getString("loginUserId", null));
        intent.putExtra("mode", "follow");
        intent.putExtra("showFollow", userName.getText() + "さんのフォロー");
        startActivity(intent);
    }
    @OnClick(R.id.follower)         //フォロワー
    void clickFollower(){
        Intent intent = new Intent(MainActivity.this, FollowActivity.class);
        intent.putExtra("referenceUserId", mainPref.getString("loginUserId", null));
        intent.putExtra("mode", "follower");
        intent.putExtra("showFollow", userName.getText() + "さんのフォロワー");
        startActivity(intent);
    }
    //スタンプ管理タブ
    @OnClick(R.id.createStampRallyButton)       //--スタンプラリー作成ボタン
    void clickCreateStampRallyButton(){
        Intent intent = new Intent(MainActivity.this, StampRallyControlActivity.class);
        startActivity(intent); }
    @OnClick(R.id.editStamp)                    //--スタンプ編集ボタン
    void clickEditStamp(){
        Intent intent = new Intent(MainActivity.this, StampEditListActivity.class);
        startActivity(intent); }
    @OnClick(R.id.stampRegistrationButton)      //--カメラボタン
    void clickStampRegistrationButton(){
        Intent intent = new Intent(this, TakeStampActivity.class);
        intent.putExtra("stampRegisterFlag", true);
        startActivity(intent); }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchedJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:MainActivity","データベースとの通信に失敗");
            Toast.makeText(MainActivity.this, "データベースとの通信に失敗しました", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Log.d("デバッグ:MainActivity","データベースとの通信に成功");
            Users loginUser = new ObjectMapper().readValue(event.getJson(), Users.class);
            if(loginUser.getThumbnailData() != null) {
                byte[] notDecodedThumbnail = loginUser.getThumbnailData();
                Bitmap thumbnail = BitmapFactory.decodeByteArray(notDecodedThumbnail, 0, notDecodedThumbnail.length);
                profileThumbnail.setImageBitmap(thumbnail);        //プロフィール画像
                followButton.setText(loginUser.followUserCount + FOLLOW_UNIT);
                followerButton.setText(loginUser.followerCount + FOLLOW_UNIT);
            }else{
                profileThumbnail.setImageBitmap(BitmapFactory.decodeResource(getResources() ,R.mipmap.default_image_view));
            }
            userName.setText(loginUser.getUserName());
            profile.setText(loginUser.getProfile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}