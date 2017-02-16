package com.om1.stamp_rally.controller;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampDetailModel;
import com.om1.stamp_rally.model.event.FetchedJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.Overlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import database.entities.Stamps;

public class StampDetailActivity extends AppCompatActivity {
    private Stamps stamps;

    private TextView stampUserName;       //スタンプユーザー名
    private TextView stampTitle;            //スタンプ名
    private ImageView stampThumbnail;      //スタンプの画像
    private TextView stampComment;         //スタンプラリーの概要

    private Overlayer overlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_detail);

        overlayer = new Overlayer(this);
        overlayer.showProgress();

        //データベース通信
        StampDetailModel stampDetailModel = StampDetailModel.getInstance();
        if(getIntent().getStringExtra("stampId") != null){
            stampDetailModel.fetchJson(getIntent().getStringExtra("stampId"));    //通信開始
        }else{
            /* Intentの値渡しが失敗してる時に分岐 */
            Log.d("デバッグ","fetchJsonの引数の値がnullです。");
        }

        //ビュー・レイアウト
        stampTitle = (TextView) findViewById(R.id.stampTitle);
        stampThumbnail = (ImageView) findViewById(R.id.stampThumbnail);
        stampUserName = (TextView) findViewById(R.id.stampUserName);
        stampComment = (TextView) findViewById(R.id.stampComment);
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBusUtil.defaultBus.register(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        EventBusUtil.defaultBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchedJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:StampDetail","データベースとの通信に失敗");
            overlayer.hideProgress();
            return;
        }
        try {
            //Jsonをオブジェクトに変換
            stamps = new ObjectMapper().readValue(event.getJson(), Stamps.class);
            Log.d("デバッグ:StampDetail","データベースとの通信に成功");

            stampUserName.setText(stamps.getUserId().getUserName());
            stampTitle.setText(stamps.getStampName());
            stampComment.setText(stamps.getStampComment());
            stampThumbnail.setImageBitmap(BitmapFactory.decodeByteArray(stamps.getPicture(), 0, stamps.getPicture().length));

        } catch (IOException e) {
            e.printStackTrace();
        }

        overlayer.hideProgress();
    }

}
