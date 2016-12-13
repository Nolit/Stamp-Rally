package com.om1.stamp_rally.model;

import android.util.Log;

import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by b3176 on 2016/11/15.
 *
 * これは以下の動作を行うサンプル用のクラスです
 * 1. OkHttpライブラリによるサーバーとの通信
 * 2. サーバーからデータを取得後、EventBusライブラリによるアクティビティへの通知
 */

public class LoginModel {
    private final EventBus eventBus = EventBus.getDefault();
    private static LoginModel instance = new LoginModel();

    private LoginModel(){}
    public static LoginModel getInstance() {
        return instance;
    }

    //OkHttpライブラリを使用したサーバーとの通信
    public void login(String email, String password) {

        RequestBody body = new FormEncodingBuilder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/login?mailAddress="+email+"&password="+password)

                //GET通信かPOST通信か指定
                .post(body)
                .build();
        //requestに基づいた通信を別スレッドで行う
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            //URLを間違える等、サーバーに接続出来ない場合に呼ばれる
            public void onFailure(Request request, IOException e) {
                Log.d("method", "onFailure");
            }

            @Override
            //データの取得に成功したとは限らないが、サーバーに接続出来た場合に呼ばれる
            public void onResponse(Response response) throws IOException {
                Log.d("method", "onResponse");
                //イベントバスによるアクティビティへの通知
                //取得したデータを持ったイベントオブジェクトを渡す
                String a = response.body().string();

                eventBus.post(a);
            }
        });
    }

    private Map<String, String> createParameter(String email, String password){
        Map<String, String> parameter = new HashMap<>();
        parameter.put("mailAddress", email);
        parameter.put("password", password);
        return parameter;
    }
}
