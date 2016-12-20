package com.om1.stamp_rally.model;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.model.event.StampUploadEvent;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
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
    public void uploadStamp(int stampId, int stampRallyId, double latitude, double longitude, String title, String note, byte[] picture, long createDate, String email, String password){
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
        System.out.println("スタンプラリー: "+picture);
        uploadStamp(stampList, email, password);
    }

    public void uploadStamp(List<Map<String, Object>> stampList, String email, String password){
        String stampListJson;
        try {
            stampListJson = new ObjectMapper().writeValueAsString(stampList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        RequestBody body = null;
        List<Map<String, Object>> a = null;
        try {
            a = new ObjectMapper().readValue(stampListJson, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> m = a.get(0);

        String b = (String)m.get("picture");
        byte[] c1 = b.getBytes(Charset.defaultCharset());
        byte[] c2 = null;
        byte[] c3 = null;
        try {
             c2 = b.getBytes("ISO_8859_1");
             c3 = b.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("スタンプラリー: "+b.length());
        System.out.println("スタンプラリー: "+b.lastIndexOf(10));
        System.out.println("スタンプラリー: "+c1);
        System.out.println("スタンプラリー: "+c2);
        System.out.println("スタンプラリー: "+c3);

//            body = new FormEncodingBuilder()
//                    .add("stampList", stampListJson)
//                    .add("email", email)
//                    .add("password", password)
//                    .build();
            body = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .add
                    .addFormDataPart("stampList", stampListJson)
                    .addFormDataPart("sample", "あいうえお")
                    .addFormDataPart("email", email)
                    .addFormDataPart("password", password)
                    .build();
//
        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/stampUpload")
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
                Log.d("スタンプラリー", response.body().string());
//                boolean isClear = Boolean.valueOf(response.body().string());
//                EventBusUtil.defaultBus.post(new StampUploadEvent(isSuccess, isClear));
            }
        });
    }
}
