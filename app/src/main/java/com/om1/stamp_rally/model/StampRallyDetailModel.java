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

public class StampRallyDetailModel {
    private final EventBus eventBus = EventBus.getDefault();
    private static StampRallyDetailModel instance = new StampRallyDetailModel();

    private StampRallyDetailModel(){}
    public static StampRallyDetailModel getInstance() {
        return instance;
    }

    public void fetchJson(String loginUserId, String referenceUserId, String stampRallyId){

        System.out.println("デバッグ"+loginUserId);
        System.out.println("デバッグ"+referenceUserId);
        System.out.println("デバッグ"+stampRallyId);

        RequestBody body = new FormEncodingBuilder()
                .add("loginUserId", loginUserId)
                .add("referenceUserId", referenceUserId)
                .add("StampRallyId", stampRallyId)
                .build();

        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/StampRallyDetail?loginUserId="+loginUserId+"&referenceUserId="+referenceUserId+"&stampRallyId="+stampRallyId)
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