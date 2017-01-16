package com.om1.stamp_rally.controller;

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
import com.om1.stamp_rally.model.event.StampRallyDetailEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import data.StampData;
import data.StampRallyDetailPageData;
import database.entities.StampRallys;
import database.entities.Stamps;

public class StampRallyDetailActivity extends AppCompatActivity {
    SharedPreferences mainPref;
    private String loginUserId;
    private String referenceUserId;
    private String stampRallyId;

    private final EventBus eventBus = EventBus.getDefault();

    private TextView creatorUserName;           //スタンプラリー作成名
    private TextView stampRallyTitle;           //参照しているスタンプラリー名
    private ImageButton stampThumbnail;         //スタンプの画像
    private TextView stampTitle;                //スタンプ名
    private TextView referenceUserName;        //表示するスタンプのアルバム所有者
    private TextView stampRallyDescription;    //スタンプラリーの概要
    private TextView stampRallyReviewAveragePoint;  //評価の平均値
    private ImageButton stampEvaluationButton;  //評価ボタン
    private Button playButton;                  //遊ぶボタン

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_detail);
        eventBus.register(this);

        //データベース通信
        mainPref = getSharedPreferences("main", MODE_PRIVATE);
        loginUserId = mainPref.getString("loginUserId", null);             //ログイン中のUserId (Preferencesを使用)
        referenceUserId = getIntent().getStringExtra("referenceUserId");   //表示するスタンプのUserId (getIntentを使用)
        stampRallyId = getIntent().getStringExtra("stampRallyId");         //表示するスタンプラリーのId (getIntentを使用)

        StampRallyDetailModel stampRallyDetailModel = StampRallyDetailModel.getInstance();
        if(loginUserId != null && referenceUserId != null && stampRallyId != null){
            stampRallyDetailModel.fetchJson(loginUserId, referenceUserId, stampRallyId);    //通信開始
        }else{
            /* mainPrefかIntentの値渡しが失敗してる時に分岐 */
            Log.d("デバッグ","fetchJsonの引数の値がnullです。");
            System.out.println("loginUserId:" + loginUserId);
            System.out.println("referenceUserId:" + referenceUserId);
            System.out.println("stampRallyId:" + stampRallyId + "_________________");
        }

        //ビュー・レイアウト
        creatorUserName = (TextView) findViewById(R.id.CreatorName);
        stampRallyTitle = (TextView) findViewById(R.id.DetailStampTitle);
        referenceUserName = (TextView) findViewById(R.id.DetailReferenceName);
        stampRallyDescription = (TextView) findViewById(R.id.DescriptionText);
        stampRallyReviewAveragePoint = (TextView) findViewById(R.id.ReviewPoint);
        stampEvaluationButton = (ImageButton) findViewById(R.id.EvaluationButton);
        playButton = (Button) findViewById(R.id.PlayButton);

    }

    //データベース通信処理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(StampRallyDetailEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:StampRallyDetail","データベースとの通信に失敗");
            return;
        }
        try {
            Log.d("デバッグ:StampRallyDetail","データベースとの通信に成功");
            String[] responseData = event.getJson().split(System.getProperty("line.separator"));
            StampRallyDetailPageData pageData = new ObjectMapper().readValue(responseData[0], StampRallyDetailPageData.class);
            StampData[] stampData = new ObjectMapper().readValue(responseData[1], StampData[].class);

            //ページ全体 データ設定
            System.out.println("作成者:"+ pageData.getStampRallyCreatorsUserName());
            System.out.println("スタンプラリータイトル:"+ pageData.getStampRallyTitle());
            System.out.println("参照ユーザー名:"+ pageData.getReferenceUserName());
            System.out.println("コメント:"+ pageData.getStampRallyComment());
            creatorUserName.setText(pageData.getReferenceUserName());
            stampRallyTitle.setText(pageData.getStampRallyTitle());
            referenceUserName.setText(pageData.getReferenceUserName());
            stampRallyDescription.setText(pageData.getStampRallyComment());
//            stampRallyReviewAveragePoint.setText(pageData.getStampRallyReviewPoint());

            //スタンプサムネイルを一覧で横スクロール表示
            LinearLayout layout = (LinearLayout) findViewById(R.id.DetailLinearLayoutAddThumbnail);
            for (StampData index:stampData) {
                View view = getLayoutInflater().inflate(R.layout.list_stmaprally_detail_thumbnail, null);
                layout.addView(view);

                //スタンプ画像
                stampThumbnail = (ImageButton) view.findViewById(R.id.stampThumbnail);
                Bitmap picture = BitmapFactory.decodeByteArray(index.getPicture(), 0, index.getPicture().length);
                stampThumbnail.setImageBitmap(picture);
                //スタンプ名
                stampTitle = (TextView) view.findViewById(R.id.stampTitle);
                stampTitle.setText(index.getStampName());
            }



            //評価ボタンの設定

            //遊ぶボタンの設定設定
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
