package com.om1.stamp_rally.model;

import com.om1.stamp_rally.model.event.FetchStampRallyEvent;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class MyStampBookModel {
    private final EventBus eventBus = EventBus.getDefault();
    private static MyStampBookModel instance = new MyStampBookModel();

    private MyStampBookModel(){}
    public static MyStampBookModel getInstance() {
        return instance;
    }

    public void fetchJson(String referenceUserId) {
        RequestBody body = new FormEncodingBuilder()
                .add("referenceUserId", referenceUserId)
                .build();

        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/MyStampBook?referenceUserId="+referenceUserId)
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                eventBus.post(new FetchStampRallyEvent(false,null));
            }
            @Override
            public void onResponse(Response response) throws IOException {
                eventBus.post(new FetchStampRallyEvent(response.isSuccessful(), response.body().string()));
            }
        });
    }
}