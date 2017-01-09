package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampRallyDetailModel;
import com.om1.stamp_rally.model.StampRallyModel;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.event.FetchJsonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import database.entities.StampRallys;
import database.entities.Stamps;

import static com.om1.stamp_rally.R.id.stampTitle;

public class StampRallyDetailActivity extends AppCompatActivity {
    SharedPreferences mainPref;
    private Intent acceptIntent;
    private String userId;
    private String referenceUserId;
    private String stampRallyId;

    private final EventBus eventBus = EventBus.getDefault();
    private StampRallys stampRally;

    private Button playButton;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_detail);
        eventBus.register(this);

        //データベース通信
        getSharedPreferences("main", MODE_PRIVATE);
        userId = mainPref.getString("loginUserId", null);                   //ログイン中のUserId

        acceptIntent = getIntent();
        referenceUserId = acceptIntent.getStringExtra("referenceUserId");   //表示するスタンプのUserId
        stampRallyId = acceptIntent.getStringExtra("StampRallyId");         //表示するスタンプラリーのId

        StampRallyDetailModel stampRallyDetailModel = StampRallyDetailModel.getInstance();
        stampRallyDetailModel.fetchJson(userId, referenceUserId, stampRallyId);    //通信開始

        //ビュー・レイアウト
        description = (TextView) findViewById(R.id.DescriptionText);    //概要
        playButton = (Button) findViewById(R.id.PlayButton);            //遊ぶボタン

    }

    //データベース通信処理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ","データベースとの通信に失敗");
            return;
        }
        try {
            //Jsonをオブジェクトに変換
            stampRally = new ObjectMapper().readValue(event.getJson(), StampRallys.class);
            Log.d("デバッグ","データベースとの通信に成功");
            System.out.println("デバッグ"+stampRally.getStamprallyName()+0);

            for(Stamps stamps:stampRally.getStampList()){
                System.out.println("デバッグ:詳細:" + stamps.getStampName());
            }

            //サムネイルを横スクロールで表示
            LinearLayout layout = (LinearLayout) findViewById(R.id.DetailLinearLayoutAddThumbnail);
            for (Stamps stamps:stampRally.getStampList()) {
                View view = getLayoutInflater().inflate(R.layout.sub_stmaprally_detail_thumbnail, null);
                layout.addView(view);
                ImageButton stampThumbnail = (ImageButton) view.findViewById(R.id.stampThumbnail);

                /* ここにImageButtonの画像セット処理を追加する */

                TextView stampTitle = (TextView) view.findViewById(R.id.stampTitle);
                stampTitle.setText(stamps.getStampName());
            }

            //概要を設定
            description.setText(stampRally.getStamrallyComment());

            //遊ぶボタンを設定
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(StampRallyDetailActivity.this.getApplication(), "スタンプラリーのマーカーをマップにセットしました", Toast.LENGTH_LONG).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
