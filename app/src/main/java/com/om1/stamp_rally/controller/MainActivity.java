package com.om1.stamp_rally.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.LoginModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity  extends FragmentActivity implements OnMapReadyCallback {
    private static final String LOGOUT_SENTENCE = "ログアウトしました";
    //tabHost
    @InjectView(R.id.tabHost)
    TabHost th;
    //トップ
    Button searchButton;
    EditText search;
    //タイムライン
    //マップ
    FragmentManager mapFragmentManager;

    //テスト用ボタン - スタンプ管理タブ
    Button stampRallyDetailIntentButton;    //スタンプラリー詳細ページ
    Button intentButtonMyStampList;         //マイスタンプ帳ページ
    Button intentButtonStampDetail;         //スタンプ詳細ページ

    SharedPreferences mainPref;             //ログアウト時にPreferencesは削除する
    Bundle savedInstanceState;
    private final EventBus eventBus = EventBus.getDefault();

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
        eventBus.register(this);
        mapFragmentManager = getSupportFragmentManager();
        this.savedInstanceState = savedInstanceState;

        //テスト用___________________
        stampRallyDetailIntentButton = (Button) findViewById(R.id.StampRallyDetailIntentButton);
        stampRallyDetailIntentButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StampRallyDetailActivity.class);
                intent.putExtra("referenceUserId", "20");
                intent.putExtra("stampRallyId", "5");
                startActivity(intent);
            }
        });
        //テスト用ボタン - マイスタンプ帳
        intentButtonMyStampList = (Button) findViewById(R.id.IntentButton_MyStampBook);
        intentButtonMyStampList.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyStampBookActivity.class);
                intent.putExtra("referenceUserId", "20");
                startActivity(intent);
            }
        });
        //テスト用ボタン - スタンプ詳細ページ
        intentButtonStampDetail = (Button) findViewById(R.id.IntentButton_StampDetail);
        intentButtonStampDetail.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StampDetailActivity.class);
                intent.putExtra("stampId", "108");
                startActivity(intent);
            }
        });
        SharedPreferences.Editor mainEdit = mainPref.edit();
        mainEdit.putString("loginUserId", "21");        //端末でログインしてるユーザーのIDを保存
        //テスト用ここまで___________________

        //キーボード非表示
        search = (EditText) findViewById(R.id.SearchEdit);
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        //スタンプラリーページを選択時のフラグメント起動
        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("PLAY") && mapFragmentManager.findFragmentById(R.id.StampRally) == null){
                    mapFragmentManager.beginTransaction().add(R.id.StampRally, new MapsFragment()).commit();
                }
            }
        });

        //検索
        searchButton = (Button) findViewById(R.id.SearchBt);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search = (EditText) findViewById(R.id.SearchEdit);

                //検索結果一覧ページへ
                Intent intent = new Intent(MainActivity.this, ResultSearchActivity.class);
                intent.putExtra("searchKeyword", search.getText().toString());
                startActivity(intent);
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
            spec = tabHost.newTabSpec("MyPage")
                    .setIndicator("HOME", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.MyPage);
            tabHost.addTab(spec);
            // タイムラインタブ
            spec = tabHost.newTabSpec("タイムライン")
                    .setIndicator("TIME", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.TimeLine);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LoginAuthentication(String loginText) {
        try {
            Boolean isLogin = new ObjectMapper().readValue(loginText, Boolean.class);
            if(isLogin == true){
                Toast.makeText(this, "ログインに成功しました", Toast.LENGTH_LONG).show();
                Intent intent=new Intent();
                intent.setClass(this, this.getClass());
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(this, "ログインに失敗しました", Toast.LENGTH_LONG).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        //EventBusライブラリによる自身の登録解除
        eventBus.unregister(this);
        super.onPause();
    }

    //マイページ
    //ログアウトボタン
    @OnClick(R.id.LogoutBt)
    void clickLogoutBt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("ログアウトしますか？")
                .setPositiveButton("ログアウト", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor mainEdit = mainPref.edit();
                        mainEdit.clear();
                        mainEdit.commit();
                        Toast.makeText(MainActivity.this, LOGOUT_SENTENCE, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
        builder.show();
    }

    //スタンプ管理タブ
    //スタンプラリー作成ボタン
    @OnClick(R.id.createStampRallyButton)
    void clickCreateStampRallyButton(){
        Intent intent = new Intent(MainActivity.this, StampRallyCreateActivity.class);
        startActivity(intent); }
    //スタンプ編集ボタン
    @OnClick(R.id.editStamp)
    void clickEditStamp(){
        Intent intent = new Intent(MainActivity.this, StampEditListActivity.class);
        startActivity(intent); }
    //カメラボタン
    @OnClick(R.id.stampRegistrationButton)
    void clickStampRegistrationButton(){
        Intent intent = new Intent(this, TakeStampActivity.class);
        intent.putExtra("stampRegisterFlag", true);
        startActivity(intent); }


}
