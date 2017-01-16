package com.om1.stamp_rally.model;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.model.event.FetchStampRallyEvent;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by yaboo on 2017/01/17.
 */

public class CreatedStampRallyUploader {
    private final EventBus eventBus = EventBus.getDefault();
    private static CreatedStampRallyUploader instance = new CreatedStampRallyUploader();

    private CreatedStampRallyUploader(){}

    public static CreatedStampRallyUploader getInstance() {
        return instance;
    }

    public void postStampRally(String userId, String title, String summary, List<Integer> selectedStampIdList, Integer thumbnailStampId) throws JsonProcessingException {
        Log.d("スタンプラリー", thumbnailStampId.toString());
        RequestBody body = new FormEncodingBuilder()
                .add("userId", userId)
                .add("title", title)
                .add("summary", summary)
                .add("selectedStampIdList", new ObjectMapper().writeValueAsString(selectedStampIdList))
                .add("thumbnailStampId", thumbnailStampId.toString())
                .build();

        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/createdStampRallyUpload")
                .post(body)
                .build();

        Log.d("スタンプラリー", "post");
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                EventBusUtil.defaultBus.post(false);
            }
            @Override
            public void onResponse(Response response) throws IOException {
                EventBusUtil.defaultBus.post(response.isSuccessful());
            }
        });
    }
}
