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
import com.om1.stamp_rally.model.LoginModel;
import com.om1.stamp_rally.model.event.LoginEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import database.entities.Users;

public class LoginActivity extends AppCompatActivity {
    private static final String ERR_VALIDATION_CHECK = "入力された値が正しくありません";
    private static final String ERR_CHALLENGE_LOGIN = "メールアドレスまたはパスワードが一致しません";
    private static final String SUCCESS_LOGIN = "ログインに成功しました";

    private final EventBus eventBus = EventBus.getDefault();
    SharedPreferences mainPref;
    SharedPreferences.Editor mainEdit;

    private EditText editMailAddress;
    private EditText editPassword;
    private String challengeMailAddress;
    private String challengePassword;

    private Button loginButton;
    private Button createAccountIntentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        eventBus.register(this);
        mainPref = getSharedPreferences("main", MODE_PRIVATE);
        mainEdit = mainPref.edit();

        editMailAddress = (EditText) findViewById(R.id.mailAddress);
        editPassword = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                challengeMailAddress = editMailAddress.getText().toString();
                challengePassword = editPassword.getText().toString();
                if(validationCheck(challengeMailAddress, challengePassword)){
                    challengeLogin(challengeMailAddress, challengePassword);
                }else{
                    Toast.makeText(LoginActivity.this, ERR_VALIDATION_CHECK, Toast.LENGTH_SHORT).show();
                }
            }
        });

        createAccountIntentButton = (Button) findViewById(R.id.createAccountIntentButton);
        createAccountIntentButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, NewMemberActivity.class));
                finish();
            }
        });

    }

    @Override
    public void onPause(){
        super.onPause();
        eventBus.unregister(this);
    }

    private void challengeLogin(String challengeMailAddress, String challengePassword){
        LoginModel loginModel = LoginModel.getInstance();
        loginModel.fetchJson(challengeMailAddress, challengePassword);
    }

    private boolean validationCheck(String challengeMailAddress, String challengePassword){
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LoginAuthentication(LoginEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:LoginActivity","データベースとの通信に失敗");
            Toast.makeText(LoginActivity.this, "データベースとの通信に失敗しました", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Log.d("デバッグ:LoginActivity","データベースとの通信に成功");
            Users loginUser = new ObjectMapper().readValue(event.getJson(), Users.class);

            if(loginUser != null){
                Toast.makeText(LoginActivity.this, SUCCESS_LOGIN, Toast.LENGTH_SHORT).show();
                mainEdit.putString("mailAddress", loginUser.getMailAddress());
                mainEdit.putString("password", loginUser.getPassword());
                mainEdit.putString("loginUserId", loginUser.getUserId().toString());
                mainEdit.commit();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }else{
                Toast.makeText(LoginActivity.this, ERR_CHALLENGE_LOGIN, Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

