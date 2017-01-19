package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.om1.stamp_rally.R;

public class LoginActivity extends AppCompatActivity {
    private static final String ERR_VALIDATION_CHECK = "入力された値が正しくありません";
    private static final String ERR_CHALLENGE_LOGIN = "メールアドレスまたはパスワードが一致しません";
    private static final String SUCCESS_LOGIN = "ログインに成功しました";

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
        mainPref = getSharedPreferences("main", MODE_PRIVATE);
        mainEdit = mainPref.edit();

        editMailAddress = (EditText) findViewById(R.id.mailAddress);
        editPassword = (EditText) findViewById(R.id.password);
        challengeMailAddress = editMailAddress.getText().toString();
        challengePassword = editPassword.getText().toString();

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(validationCheck(challengeMailAddress, challengePassword)){

                    if(challengeLogin(challengeMailAddress, challengePassword)){
                        Toast.makeText(LoginActivity.this, SUCCESS_LOGIN, Toast.LENGTH_SHORT).show();
                        mainEdit.putString("mailAddress", challengeMailAddress);
                        mainEdit.putString("password", challengePassword);
                        mainEdit.commit();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, ERR_CHALLENGE_LOGIN, Toast.LENGTH_SHORT).show();
                    }
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

    private boolean challengeLogin(String challengeMailAddress, String challengePassword){
        return true;
    }

    private boolean validationCheck(String challengeMailAddress, String challengePassword){
        return true;
    }

}

