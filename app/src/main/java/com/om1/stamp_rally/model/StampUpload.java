package com.om1.stamp_rally.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.model.event.StampUploadEvent;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yaboo on 2016/12/11.
 */

public class StampUpload {
    private static StampUpload instance = new StampUpload();

    private StampUpload(){}
    public static StampUpload getInstance() {
        return instance;
    }

    //OkHttpライブラリを使用したサーバーとの通信
    public void uploadStamp(int stampId, int stampRallyId, double latitude, double longitude, String title, String note, byte[] picture, long createDate){
        List<Map<String, Object>> stampList = new ArrayList<>();
        Map<String, Object> stamp = new HashMap<>();
        stamp.put("stampId", stampId);
        stamp.put("stampRallyId", stampRallyId);
        stamp.put("latitude", latitude);
        stamp.put("longitude", longitude);
        stamp.put("title", title);
        stamp.put("note", note);
        stamp.put("picture", picture);
        stamp.put("createDate", createDate);
        stampList.add(stamp);

        uploadStamp(stampList);
    }

    public void uploadStamp(List<Map<String, Object>> stampList){
        String stampListJson;
        try {
            stampListJson = new ObjectMapper().writeValueAsString(stampList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("json"), stampListJson);

        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/uploadStamp")
                .post(body)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                boolean isSuccess = false;
                boolean isClear = false;
                EventBusUtil.defaultBus.post(new StampUploadEvent(isSuccess, isClear));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                boolean isSuccess = response.isSuccessful();
                boolean isClear = Boolean.valueOf(response.body().string());
                EventBusUtil.defaultBus.post(new StampUploadEvent(isSuccess, isClear));
            }
        });
    }
}
