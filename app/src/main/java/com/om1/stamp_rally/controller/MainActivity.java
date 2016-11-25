package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.om1.stamp_rally.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity  extends FragmentActivity implements OnMapReadyCallback {

    Button loginButton;
    TextView newloginText;

    @InjectView(R.id.tabHost)
    TabHost th;


    //test
    //Button test1;
    //EditText test2;
    //TextView test3;
    //String a = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //ログイン画面へ
        loginButton = (Button)findViewById(R.id.login);
        loginButton.setText("ログイン");

        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //新規会員登録ページへ
        newloginText = (TextView) findViewById(R.id.newMem);
        newloginText.setText("新規会員登録");

        newloginText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewMemberActivity.class);
                startActivity(intent);
            }
        });

        th.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId) {
                Log.d("tab", tabId);
                getSupportFragmentManager().beginTransaction().add(R.id.StampRally, new MapsFragment()).commit();
            }
        });


















        //test
        //test1 = (Button)findViewById(R.id.button4);
        //test2 = (EditText) findViewById(R.id.editText);
        //test3 = (TextView) findViewById(R.id.textView2);

        //test1.setText("テスト");
        //test1.setOnClickListener(new View.OnClickListener(){
        //    public void onClick(View v) {
        //        test3.setText(test2.getText());
        //    }
        //});

        // TabHostの初期化および設定処理
        initTabs();
    }


    protected void initTabs() {
        try {
            TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
            tabHost.setup();
            TabHost.TabSpec spec;

            // Tab1
            spec = tabHost.newTabSpec("TOP")
                    .setIndicator("トップ", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.TOP);
            tabHost.addTab(spec);

            // Tab2
            spec = tabHost.newTabSpec("マイページ")
                    .setIndicator("HOME", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.MyPage);
            tabHost.addTab(spec);

            // Tab3
            spec = tabHost.newTabSpec("TimeLine")
                    .setIndicator("タイムライン", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.TimeLine);
            tabHost.addTab(spec);

            // Tab4
            spec = tabHost.newTabSpec("StampRally")
                    .setIndicator("スタンプラリー", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.StampRally);
            tabHost.addTab(spec);

            // Tab5
            spec = tabHost.newTabSpec("StampRegistration")
                    .setIndicator("スタンプ登録", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
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
}
