package com.om1.stamp_rally;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    TextView newloginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button)findViewById(R.id.login);
        loginButton.setText("ログイン");

        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        newloginText = (TextView) findViewById(R.id.newMem);
        newloginText.setText("新規会員登録");

        newloginText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewMember.class);
                startActivity(intent);
            }
        });

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
                    .setIndicator("TOP", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.TOP);
            tabHost.addTab(spec);

            // Tab2
            spec = tabHost.newTabSpec("MyPage")
                    .setIndicator("HOME", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.MyPage);
            tabHost.addTab(spec);

            // Tab3
            spec = tabHost.newTabSpec("TimeLine")
                    .setIndicator("タイムライン", ContextCompat.getDrawable(this, R.drawable.abc_menu_hardkey_panel_mtrl_mult))
                    .setContent(R.id.TimeLine);
            tabHost.addTab(spec);

            tabHost.setCurrentTab(0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

}
