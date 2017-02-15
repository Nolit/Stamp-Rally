package com.om1.stamp_rally.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.SettingsModel;
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

public class SettingsActivity extends AppCompatActivity {
    private static final String ERR_VALIDATION_CHECK_NULL = "入力項目に誤りがあります";
    private static final String ERR_OVERLAP_SERACH_ID = "そのIDはすでに使われています";
    private static final String ERR_OVERLAP_MAILADDRESS = "そのメールアドレスはすでに使われています" ;
    private static final String SUCCESS_UPDATE_DATA = "ユーザー情報を更新しました";
    private static final String LOGOUT_SENTENCE = "ログアウトしました";

    private final EventBus eventBus = EventBus.getDefault();
    SharedPreferences mainPref;             //ログアウト時にPreferencesは削除する
    SharedPreferences.Editor mainEdit;

    private boolean clickUpdateButtonFlag = false;

    @InjectView(R.id.profileThumbnail)
    ImageView profileThumbnail;
    @InjectView(R.id.searchId)
    EditText searchId;
    @InjectView(R.id.userName)
    EditText userName;
    @InjectView(R.id.profileSentence)
    EditText profileSentence;
    @InjectView(R.id.mailAddress)
    EditText mailAddress;
    @InjectView(R.id.password)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);
        mainPref = getSharedPreferences("main", MODE_PRIVATE);

        if(validationCheck()){
            SettingsModel.getInstance().fetchJson(mainPref.getString("mailAddress", null), mainPref.getString("password", null));
        }

    }

    //バリデーションチェック
    private boolean validationCheck(){
        return true;
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
            Log.d("デバッグ:SettingsActivity","データベースとの通信に失敗");
            Toast.makeText(SettingsActivity.this, "データベースとの通信に失敗しました", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Log.d("デバッグ:SettingsActivity","データベースとの通信に成功");
            Users loginUser = new ObjectMapper().readValue(event.getJson(), Users.class);

            if(clickUpdateButtonFlag){
                if(loginUser.getSearchId() == null){
                    Toast.makeText(SettingsActivity.this, ERR_OVERLAP_SERACH_ID, Toast.LENGTH_SHORT).show();
                    return;
                }else if(loginUser.getMailAddress() == null){
                    Toast.makeText(SettingsActivity.this, ERR_OVERLAP_MAILADDRESS, Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(SettingsActivity.this, SUCCESS_UPDATE_DATA, Toast.LENGTH_SHORT).show();
                    mainEdit.putString("mailAddress", loginUser.getMailAddress());
                    mainEdit.putString("password", loginUser.getPassword());
                    mainEdit.commit();
                }
            }else{
                searchId.setText(loginUser.getSearchId());
                userName.setText(loginUser.getUserName());
                profileSentence.setText(loginUser.getProfile());
                mailAddress.setText(loginUser.getMailAddress());
                password.setText(loginUser.getPassword());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.updateButton)
    void clickUpdatebutton(){
        clickUpdateButtonFlag = true;
        SettingsModel.getInstance().updateUserInfo(
                searchId.getText().toString(),
                userName.getText().toString(),
                profileSentence.getText().toString(),
                mailAddress.getText().toString(),
                password.getText().toString()
        );
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

    @OnClick(R.id.changeThumbnail)
    void clickChangeThumbnail(){
        //　処理を追加する
    }

}
