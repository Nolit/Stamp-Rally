package com.om1.stamp_rally.model;

import android.os.AsyncTask;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import database.entities.Sample;

/**
 * Created by karin757 on 2016/11/06.
 */
public class SampleAsyncTask extends AsyncTask<Integer, String, String>{
    private TextView tv;

    public SampleAsyncTask(TextView tv){
        this.tv = tv;
    }
    @Override
    protected String doInBackground(Integer... params) {
        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/sample")
                .get()
                .build();

        String responseData = null;
        try {
            responseData = new OkHttpClient().newCall(request).execute().body().string();
            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {

                }
            });
            System.out.println(responseData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }

    @Override
    protected void onPostExecute(String json){
        ObjectMapper mapper = new ObjectMapper();
        Sample sample;
        try {
            sample = mapper.readValue(json, Sample.class);
            tv.setText(sample.getId() + ":"+sample.getEmail()+":"+sample.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
