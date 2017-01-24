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
import java.util.concurrent.TimeUnit;

public class StampDetailModel {
    private final EventBus eventBus = EventBus.getDefault();
    private static StampDetailModel instance = new StampDetailModel();

    private StampDetailModel(){}
    public static StampDetailModel getInstance() {
        return instance;
    }

    public void fetchJson(String stampId){
        RequestBody body = new FormEncodingBuilder()
                .add("stampId", stampId)
                .build();

        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/StampDetail?stampid="+stampId)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(25, TimeUnit.SECONDS);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                eventBus.post(new FetchedJsonEvent(false,null));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                eventBus.post(new FetchedJsonEvent(response.isSuccessful(), response.body().string()));
            }
        });
    }
}
