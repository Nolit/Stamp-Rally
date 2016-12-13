package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
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

public class MainActivity  extends FragmentActivity implements OnMapReadyCallback {

    //ログイン
    Button loginButton;
    Button SearchButton;
    TextView newloginText;
    EditText id;
    EditText pass;

    SharedPreferences mainPref;

    //tabHost
    @InjectView(R.id.tabHost)
    TabHost th;

    //リロード
    Bundle savedInstanceState;

    int i = 0;

    private final EventBus eventBus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        eventBus.register(this);
        mainPref = getSharedPreferences("main", MODE_PRIVATE);

        //インスタンスの保存
        this.savedInstanceState = savedInstanceState;

        //ログイン
        loginButton = (Button)findViewById(R.id.LoginBt);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                id = (EditText) findViewById(R.id.emailAddress);
                pass = (EditText) findViewById(R.id.password);

                SharedPreferences.Editor mainEdit = mainPref.edit();
                mainEdit.putString("mailAddress", id.getText().toString());
                mainEdit.putString("password", pass.getText().toString());
                mainEdit.commit();

                LoginModel.getInstance().login(id.getText().toString(), pass.getText().toString());
            }
        });

        //新規会員登録ページへ
        newloginText = (TextView) findViewById(R.id.newmember);
        newloginText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewMemberActivity.class);
                startActivity(intent);
            }
        });

        //スタンプラリーページを選択時のフラグメント起動
        th.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId) {
                Log.d("tab", tabId);
                getSupportFragmentManager().beginTransaction().add(R.id.StampRally, new MapsFragment()).commit();
            }
        });


        String useId = mainPref.getString("mailAddress", null);

        //ログイン時とゲスト時のtabHostメソッド分け
        if(useId != null) {
            initLoginTabs();
        }
        else{
            initGuestTabs();
        }


        //検索
        SearchButton = (Button)findViewById(R.id.SearchBt);
        SearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }


    //ログイン時のtabHost
    protected void initLoginTabs() {
        try {
            TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
            tabHost.setup();
            TabHost.TabSpec spec;

            // TOPページタブ
            spec = tabHost.newTabSpec("TOP")
                    .setIndicator("トップ", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.TOP);
            tabHost.addTab(spec);

            // マイページタブ
            spec = tabHost.newTabSpec("MyPage")
                    .setIndicator("マイページ", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.MyPage);
            tabHost.addTab(spec);

            // タイムラインタブ
            spec = tabHost.newTabSpec("タイムライン")
                    .setIndicator("TIME", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.TimeLine);
            tabHost.addTab(spec);

            // スタンプラリータブ
            spec = tabHost.newTabSpec("スタンプラリー")
                    .setIndicator("PLAY", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.StampRally);
            tabHost.addTab(spec);

            // スタンプ登録タブ
            spec = tabHost.newTabSpec("スタンプ")
                    .setIndicator("STAMP", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.StampRegistration);
            tabHost.addTab(spec);

            //ログインタブ
            spec = tabHost.newTabSpec("Login")
                    .setIndicator("ログイン", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.Login);
            //tabHost.addTab(spec);

            tabHost.setCurrentTab(0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    //ゲスト時のtabHost
    protected void initGuestTabs() {
        try {
            TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
            tabHost.setup();
            TabHost.TabSpec spec;

            // TOPページタブ
            spec = tabHost.newTabSpec("TOP")
                    .setIndicator("トップ", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.TOP);
            tabHost.addTab(spec);

            // マイページタブ
            spec = tabHost.newTabSpec("マイページ")
                    .setIndicator("HOME", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.MyPage);
            //tabHost.addTab(spec);

            // タイムラインタブ
            spec = tabHost.newTabSpec("TimeLine")
                    .setIndicator("タイムライン", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.TimeLine);
            //tabHost.addTab(spec);

            // スタンプラリータブ
            spec = tabHost.newTabSpec("StampRally")
                    .setIndicator("スタンプラリー", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.StampRally);
            //tabHost.addTab(spec);

            // スタンプ登録タブ
            spec = tabHost.newTabSpec("StampRegistration")
                    .setIndicator("スタンプ登録", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.StampRegistration);
            //tabHost.addTab(spec);

            //ログインタブ
            spec = tabHost.newTabSpec("Login")
                    .setIndicator("ログイン", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.Login);
            tabHost.addTab(spec);
            tabHost.setCurrentTab(0);

            //タブ切り替え時にアクディビディ再読み込み
//            tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
//                @Override
//                public void onTabChanged(String tabId) {
//                    MainActivity.this.onStart();
//                }
//            });
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
}
