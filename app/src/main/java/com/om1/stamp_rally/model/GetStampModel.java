package com.om1.stamp_rally.model;

import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yaboo on 2016/12/06.
 */

public class GetStampModel {

    private static GetStampModel instance = new GetStampModel();

    private GetStampModel(){}
    public static GetStampModel getInstance() {
        return instance;
    }

    public void postGotStamp(String title, String note, byte[] picture){
        String json = createParameterAsJson(title, note, picture);
        RequestBody body = RequestBody.create(MediaType.parse("json"), json);
        final Request request = new Request.Builder()
            .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/getStamp")
            .post(body)
            .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new OkHttpClient().newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String createParameterAsJson(String title, String note, byte[] picture){
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("title", title);
        parameter.put("note", note);
        parameter.put("picture", picture);
        return new JSONObject(parameter).toString();
    }
}
