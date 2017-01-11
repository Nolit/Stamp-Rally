package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampRallyDetailModel;
import com.om1.stamp_rally.model.event.FetchJsonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import database.entities.StampRallys;
import database.entities.Stamps;

public class StampRallyDetailActivity extends AppCompatActivity {
    SharedPreferences mainPref;
    private String loginUserId;
    private String referenceUserId;
    private String stampRallyId;

    private final EventBus eventBus = EventBus.getDefault();
    private StampRallys stampRally;

    private TextView creatorUserName;           //スタンプラリー作成名
    private TextView stampRallyTitle;           //参照しているスタンプラリー名
    private ImageButton stampThumbnail;         //スタンプの画像
    private TextView stampTitle;                //スタンプ名
    private TextView referenceUserName;        //表示するスタンプのアルバム所有者
    private TextView stampRallyDescription;    //スタンプラリーの概要
    private Button playButton;                  //遊ぶボタン

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_detail);
        eventBus.register(this);

        //データベース通信
        mainPref = getSharedPreferences("main", MODE_PRIVATE);
        loginUserId = mainPref.getString("loginUserId", null);             //ログイン中のUserId (Preferencesを使用)
//        referenceUserId = getIntent().getStringExtra("referenceUserId");   //表示するスタンプのUserId (getIntentを使用)
//        stampRallyId = getIntent().getStringExtra("StampRallyId");         //表示するスタンプラリーのId (getIntentを使用)
        referenceUserId = "20";  //テスト用に値を指定
        stampRallyId = "4";     //テスト用に値を指定

        StampRallyDetailModel stampRallyDetailModel = StampRallyDetailModel.getInstance();

        if(loginUserId != null && referenceUserId != null && stampRallyId != null){
            stampRallyDetailModel.fetchJson(loginUserId, referenceUserId, stampRallyId);    //通信開始
        }else{
            /* mainPrefかIntentの値渡しが失敗してる時に分岐 */
            Log.d("デバッグ","fetchJsonの引数の値がnullです。");
        }

        //ビュー・レイアウト
        creatorUserName = (TextView) findViewById(R.id.CreatorName);
        stampRallyTitle = (TextView) findViewById(R.id.DetailStampTitle);
        referenceUserName = (TextView) findViewById(R.id.DetailReferenceName);
        stampRallyDescription = (TextView) findViewById(R.id.DescriptionText);
        playButton = (Button) findViewById(R.id.PlayButton);

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
            for(Stamps stamps:stampRally.getStampList()){
                System.out.println("デバッグ:詳細ページ:" + stamps.getStampName());
            }

            //スタンプラリー作成者
            creatorUserName.setText(stampRally.getUsersList().get(0).getUserName());

            //スタンプラリーの評価値

            //スタンプラリー評価ボタン

            //スタンプラリー名
            stampRallyTitle.setText(stampRally.getStamprallyName());

            //サムネイルを横スクロールで表示
            LinearLayout layout = (LinearLayout) findViewById(R.id.DetailLinearLayoutAddThumbnail);
            for (Stamps stamps:stampRally.getStampList()) {
                View view = getLayoutInflater().inflate(R.layout.sub_stmaprally_detail_thumbnail, null);
                layout.addView(view);

                //スタンプ画像
                stampThumbnail = (ImageButton) view.findViewById(R.id.stampThumbnail);
                Bitmap picture = BitmapFactory.decodeByteArray(stamps.getPicture(), 0, stamps.getPicture().length);
                stampThumbnail.setImageBitmap(picture);

                //スタンプ名
                stampTitle = (TextView) view.findViewById(R.id.stampTitle);
                stampTitle.setText(stamps.getStampName());
            }

            //参照者の名前
//            referenceUserName.setText(stampRally.);

            //概要を設定
            stampRallyDescription.setText(stampRally.getStamrallyComment());

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
