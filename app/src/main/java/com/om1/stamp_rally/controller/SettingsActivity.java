package com.om1.stamp_rally.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.om1.stamp_rally.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {
    private static final String LOGOUT_SENTENCE = "ログアウトしました";

    SharedPreferences mainPref;             //ログアウト時にPreferencesは削除する

    @InjectView(R.id.logoutButton)
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);
        mainPref = getSharedPreferences("main", MODE_PRIVATE);

    }

    @OnClick(R.id.logoutButton)
    void clickLogoutButton(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage("ログアウトしますか？")
                .setPositiveButton("ログアウト", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor mainEdit = mainPref.edit();
                        mainEdit.clear();
                        mainEdit.commit();
                        Toast.makeText(SettingsActivity.this, LOGOUT_SENTENCE, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                        finish();
                    }
                });
        builder.show();
    }
}
