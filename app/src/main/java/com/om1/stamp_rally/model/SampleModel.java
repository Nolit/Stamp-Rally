package com.om1.stamp_rally.model;

import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.utility.Url;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * Created by b3176 on 2016/11/15.
 *
 * これは以下の動作を行うサンプル用のクラスです
 * 1. OkHttpライブラリによるサーバーとの通信
 * 2. サーバーからデータを取得後、EventBusライブラリによるアクティビティへの通知
 */

public class SampleModel {
    private final EventBus eventBus = EventBus.getDefault();
    private static SampleModel instance = new SampleModel();

    private SampleModel(){}
    public static SampleModel getInstance() {
        return instance;
    }

    //OkHttpライブラリを使用したサーバーとの通信
    public void fetchJson(){
        RequestBody body = null;

        body = new FormEncodingBuilder()
        .addEncoded("sample", "あいうえ")
        .build();
//        body = new MultipartBuilder()
//                .type(MultipartBuilder.FORM)
//                .addPart(
//                        Headers.of("Content-Disposition", "form-data; name=\"sample\""),
//                        RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), "あいうえお")
//                )
//                .build();
        Request request = new Request.Builder()
                .url("http://"+ Url.HOST+":"+Url.PORT+"/stamp-rally/sample")
                //GET通信かPOST通信か指定
                .post(body)
                .build();
        //requestに基づいた通信を別スレッドで行う
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            //URLを間違える等、サーバーに接続出来ない場合に呼ばれる
            public void onFailure(Request request, IOException e) {
                //イベントバスによるアクティビティへの通知
                //通信に失敗した時の適切なイベントオブジェクトを渡す
                eventBus.post(new FetchJsonEvent(false,null));
            }

            @Override
            //データの取得に成功したとは限らないが、サーバーに接続出来た場合に呼ばれる
            public void onResponse(Response response) throws IOException {
                //イベントバスによるアクティビティへの通知
                //取得したデータを持ったイベントオブジェクトを渡す
                eventBus.post(new FetchJsonEvent(response.isSuccessful(), response.body().string()));
            }
        });
    }
}
