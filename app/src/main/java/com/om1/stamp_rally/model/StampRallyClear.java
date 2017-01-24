package com.om1.stamp_rally.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by yaboo on 2016/12/10.
 */

public class StampRallyClear {
    private final EventBus eventBus = EventBus.getDefault();
    private static StampRallyClear instance = new StampRallyClear();

    public static StampRallyClear getInstance(){
        return instance;
    }

    public void upload(List<Map<String, Object>> stampList) throws JsonProcessingException {
        RequestBody requestBody;
        requestBody = RequestBody.create(MediaType.parse("text"), new ObjectMapper().writeValueAsString(stampList));
        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/clearStampRally")
                .post(requestBody)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                eventBus.post(false);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                eventBus.post(response.isSuccessful());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void triedUpload(boolean success) {
//        if (success) {
//            Toast.makeText(this, "通信に失敗しました。", Toast.LENGTH_SHORT).show();
//            return;
//        }

    }
}
