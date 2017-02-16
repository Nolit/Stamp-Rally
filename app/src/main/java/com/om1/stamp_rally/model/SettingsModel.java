package com.om1.stamp_rally.model;

import com.om1.stamp_rally.model.event.FetchedJsonEvent;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class SettingsModel {
    private final EventBus eventBus = EventBus.getDefault();
    private static SettingsModel instance = new SettingsModel();

    private SettingsModel(){}
    public static SettingsModel getInstance() {
        return instance;
    }

    public void fetchJson(String email, String password) {

        RequestBody body = new FormEncodingBuilder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/settings")
                .post(body)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                eventBus.post(new FetchedJsonEvent(false,null));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                eventBus.post(new FetchedJsonEvent(response.isSuccessful(), response.body().string()));
            }
        });
    }

    public void updateUserInfo(String userId, String searchId, String userName
            , String profile, String email, String password) {

        RequestBody body = new FormEncodingBuilder()
                .add("userId", userId)
                .add("searchId", searchId)
                .add("userName", userName)
                .add("profile", profile)
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/updateUserInfo")
                .post(body)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                eventBus.post(new FetchedJsonEvent(false,null));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                eventBus.post(new FetchedJsonEvent(response.isSuccessful(), response.body().string()));
            }
        });
    }
}