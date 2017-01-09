package com.om1.stamp_rally.model;

import android.util.Log;

import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.model.event.FetchStampRallyEvent;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class StampRallyModel {
    private final EventBus eventBus = EventBus.getDefault();
    private static StampRallyModel instance = new StampRallyModel();

    private StampRallyModel(){}
    public static StampRallyModel getInstance() {
        return instance;
    }

    public void fetchJson(){
        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/map")
                .get()
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
