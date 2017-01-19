package com.om1.stamp_rally.model;

import android.util.Log;

import com.om1.stamp_rally.model.event.LoginEvent;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginModel {
    private final EventBus eventBus = EventBus.getDefault();
    private static LoginModel instance = new LoginModel();

    private LoginModel(){}
    public static LoginModel getInstance() {
        return instance;
    }

    public void fetchJson(String email, String password) {

        RequestBody body = new FormEncodingBuilder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/login?mailAddress="+email+"&password="+password)
                .post(body)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                eventBus.post(new LoginEvent(false,null));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                eventBus.post(new LoginEvent(response.isSuccessful(), response.body().string()));
            }
        });
    }
}