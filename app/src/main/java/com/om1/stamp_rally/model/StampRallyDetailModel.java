package com.om1.stamp_rally.model;

import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class StampRallyDetailModel {
    private final EventBus eventBus = EventBus.getDefault();
    private static StampRallyDetailModel instance = new StampRallyDetailModel();

    private StampRallyDetailModel(){}
    public static StampRallyDetailModel getInstance() {
        return instance;
    }

    public void fetchJson(String userId, String referenceUserId, String StampRallyId){
        RequestBody body = new FormEncodingBuilder()
                .add("userId", userId)
                .add("referenceUserId", referenceUserId)
                .add("StampRallyId", StampRallyId)
                .build();

        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/StampRallyDetail")
                .post(body)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                eventBus.post(new FetchJsonEvent(false,null));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                eventBus.post(new FetchJsonEvent(response.isSuccessful(), response.body().string()));
            }
        });
    }
}
