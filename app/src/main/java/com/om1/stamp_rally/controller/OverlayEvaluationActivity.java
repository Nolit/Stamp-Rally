package com.om1.stamp_rally.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampRallyDetailModel;

public class OverlayEvaluationActivity extends Activity {
    private RatingBar overlayRatingBar;
    private Button overlayEvaluationButton;

    private SharedPreferences mainPref;

    private String email;
    private String pass;
    private String stampRallyId;
    private float point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay_evaluation);

        //ページ情報
        mainPref = getSharedPreferences("main", MODE_PRIVATE);
        email = mainPref.getString("mailAddress", null);
        pass = mainPref.getString("password", null);
        stampRallyId = getIntent().getStringExtra("stampRallyId");

        //RatingBar設定
        overlayRatingBar = (RatingBar) findViewById(R.id.overlayRatingBar);
        overlayRatingBar.setRating(getIntent().getExtras().getInt("defaultPoint", 0));
        overlayRatingBar.setStepSize((float) 0.5);
        overlayRatingBar.setNumStars(5);

        overlayEvaluationButton = (Button) findViewById(R.id.overlayEvaluationButton);
        overlayEvaluationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                StampRallyDetailModel.getInstance().evaluation(email,pass,stampRallyId,(float) overlayRatingBar.getRating());
                System.out.println("デバッグ"+(float) overlayRatingBar.getRating());
                finish();
            }
        });

    }
}
