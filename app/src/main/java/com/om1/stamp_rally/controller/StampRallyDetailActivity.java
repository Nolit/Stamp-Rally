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
import com.om1.stamp_rally.model.event.FetchedJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import data.StampData;
import data.StampRallyDetailPageData;

public class StampRallyDetailActivity extends AppCompatActivity {
    private static final String SET_PLAY_STAMP_RALLY = "スタンプラリーをマップにセットしました";
    private static final String SET_FAVORITE_STAMP_RALLY = "お気に入りしました";

    SharedPreferences mainPref;
    private String loginUserId;
    private String referenceUserId;
    private String stampRallyId;
    private boolean isFavorite;
    private Integer reviewPoint;

    private final EventBus eventBus = EventBus.getDefault();

    //レイアウト・ビュー
    @InjectView(R.id.CreatorName)
    TextView creatorUserName;               //スタンプラリー作成名
    @InjectView(R.id.DetailStampTitle)
    TextView stampRallyTitle;               //参照しているスタンプラリー名
    @InjectView(R.id.DescriptionText)
    TextView stampRallyDescription;         //スタンプラリーの概要
    @InjectView(R.id.DetailReferenceName)
    TextView referenceUserName;             //表示するスタンプのアルバム所有者
    @InjectView(R.id.FavoriteButton)
    ImageButton favoriteButton;             //お気に入りボタン
    @InjectView(R.id.ReviewPoint)
    TextView stampRallyReviewAveragePoint;  //評価の平均値
    @InjectView(R.id.EvaluationButton)
    ImageButton stampEvaluationButton;      //評価ボタン
    @InjectView(R.id.PlayButton)
    Button playButton;                      //遊ぶボタン

    private ImageButton stampThumbnail;         //スタンプの画像
    private TextView stampTitle;                //スタンプ名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_detail);
        ButterKnife.inject(this);

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

    //データベース通信処理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchedJsonEvent event) {
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
            if(pageData.getStampRallyReviewAveragePoint() != null){
                stampRallyReviewAveragePoint.setText(pageData.getStampRallyReviewAveragePoint().toString());
            }else{
                stampRallyReviewAveragePoint.setText("0");
            }

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

            //お気に入りボタン
            isFavorite = pageData.isFavorite();
            if(isFavorite){
                favoriteButton.setImageBitmap(BitmapFactory.decodeResource(getResources() ,R.mipmap.ic_favorite_on));
            }else{
                favoriteButton.setImageBitmap(BitmapFactory.decodeResource(getResources() ,R.mipmap.ic_favorite_off));
            }

            //評価ボタンの設定
            if(pageData.getStampRallyCompleteDate() != null){
                stampEvaluationButton.setEnabled(true);
                if(pageData.getStampRallyReviewPoint() != null){
                    reviewPoint = pageData.getStampRallyReviewPoint();
                    stampEvaluationButton.setImageBitmap(BitmapFactory.decodeResource(getResources() ,R.mipmap.ic_evaluation_star_on));
                }
            }else{
                stampEvaluationButton.setEnabled(false);
            }

            //遊ぶボタンの設定
            String playButtonStatus = "遊ぶ";
            //プレイ中のスタンプラリーを閲覧
            if (stampRallyId.equals(mainPref.getString("playingStampRally", null))) {
                playButtonStatus = "プレイ中";
                playButton.setEnabled(false);
            }else{
                //現在プレイ中ではないスタンプラリーを閲覧
                if(pageData.getStampRallyCompleteDate() != null){
                    //クリア済みのスタンプラリーを閲覧
                    playButtonStatus = "コンプリート済み";
                    playButton.setEnabled(false);
                }else if(pageData.getStampRallyChallengeDate() != null && pageData.getStampRallyCompleteDate() == null){
                    //挑戦したことがあるスタンプラリーを閲覧
                    playButtonStatus = "再開";
                    playButton.setEnabled(true);
                }else{
                    //挑戦したことのないスタンプラリーを閲覧
                    playButtonStatus = "遊ぶ";
                    playButton.setEnabled(true);
                }
            }
            playButton.setText(playButtonStatus);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playButton.setText("プレイ中");
                    playButton.setEnabled(false);

                    SharedPreferences.Editor mainEdit = mainPref.edit();
                    mainEdit.putString("playingStampRally", stampRallyId);
                    mainEdit.commit();
                    Toast.makeText(StampRallyDetailActivity.this.getApplication(), SET_PLAY_STAMP_RALLY, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.EvaluationButton)
    void clickEvaluationButton(){
        Intent intent = new Intent(StampRallyDetailActivity.this, OverlayEvaluationActivity.class);
        intent.putExtra("defaultPoint", reviewPoint);
        intent.putExtra("stampRallyId", stampRallyId);
        startActivity(intent);
    }

    @OnClick(R.id.FavoriteButton)
    void clickFavoriteButton(){
        //現在のfavoriteの状態をサーバーに送る（trueを送ればデータベースの値は　true → false になる）
        if(isFavorite){
            //お気に入り解除
            favoriteButton.setImageBitmap(BitmapFactory.decodeResource(getResources() ,R.mipmap.ic_favorite_off));
            isFavorite = !isFavorite;
            StampRallyDetailModel.getInstance().favorite(mainPref.getString("mailAddress", null), mainPref.getString("password", null)
                    , stampRallyId, isFavorite);
        }else{
            //お気に入り登録
            favoriteButton.setImageBitmap(BitmapFactory.decodeResource(getResources() ,R.mipmap.ic_favorite_on));
            Toast.makeText(StampRallyDetailActivity.this.getApplication(), SET_FAVORITE_STAMP_RALLY, Toast.LENGTH_SHORT).show();
            isFavorite = !isFavorite;
            StampRallyDetailModel.getInstance().favorite(mainPref.getString("mailAddress", null), mainPref.getString("password", null)
                    , stampRallyId, isFavorite);
        }
    }
}
