package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.CreateAccountModel;
import com.om1.stamp_rally.model.event.FetchedJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.Overlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import database.entities.Users;

public class CreateAccountActivity extends AppCompatActivity {
    private static final String ERR_VALIDATION_CHECK_NULL = "未入力の項目があります";
    private static final String ERR_CHALLENGE_CREATE_ACCOUNT = "そのメールアドレスはすでに使われています";
    private static final String SUCCESS_CREATE_ACCOUNT = "アカウントを作成しました";

    SharedPreferences mainPref;
    SharedPreferences.Editor mainEdit;

    private EditText editMailAddress;
    private EditText editPassword;
    private EditText editUserName;
    private String challengeMailAddress;
    private String challengePassword;
    private String challengeUserName;

    private Button createAccountButton;
    private Button loginIntentButton;

    private Overlayer overlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mainPref = getSharedPreferences("main", MODE_PRIVATE);
        mainEdit = mainPref.edit();

        editMailAddress = (EditText) findViewById(R.id.mailAddress);
        editPassword = (EditText) findViewById(R.id.password);
        editUserName = (EditText) findViewById(R.id.userName);

        createAccountButton = (Button) findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                challengeMailAddress = editMailAddress.getText().toString();
                challengePassword = editPassword.getText().toString();
                challengeUserName = editUserName.getText().toString();
                if(validationCheck(challengeMailAddress, challengePassword, challengeUserName)){
                    overlayer = new Overlayer(CreateAccountActivity.this);
                    overlayer.showProgress();
                    CreateAccountModel.getInstance().fetchJson(challengeMailAddress, challengePassword, challengeUserName);
                }
            }
        });

        loginIntentButton = (Button) findViewById(R.id.loginIntentButton);
        loginIntentButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                finish();
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

    //バリデーションチェック
    private boolean validationCheck(String challengeMailAddress, String challengePassword, String challengeUserName){
        //未入力
        if(!challengeMailAddress.isEmpty() && !challengePassword.isEmpty() && !challengeUserName.isEmpty()){
            return true;
        }else{
            Toast.makeText(CreateAccountActivity.this, ERR_VALIDATION_CHECK_NULL, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LoginAuthentication(FetchedJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:CreateAccount","データベースとの通信に失敗");
            overlayer.hideProgress();
            Toast.makeText(CreateAccountActivity.this, "データベースとの通信に失敗しました", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Log.d("デバッグ:CreateAccount","データベースとの通信に成功");
            Users loginUser = new ObjectMapper().readValue(event.getJson(), Users.class);

            if(loginUser != null){
                Toast.makeText(CreateAccountActivity.this, SUCCESS_CREATE_ACCOUNT, Toast.LENGTH_SHORT).show();
                mainEdit.putString("mailAddress", loginUser.getMailAddress());
                mainEdit.putString("password", loginUser.getPassword());
                mainEdit.putString("loginUserId", loginUser.getUserId().toString());
                mainEdit.commit();
                startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
                finish();
            }else{
                Toast.makeText(CreateAccountActivity.this, ERR_CHALLENGE_CREATE_ACCOUNT, Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        overlayer.hideProgress();
    }

}
