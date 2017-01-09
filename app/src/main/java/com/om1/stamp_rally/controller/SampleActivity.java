package com.om1.stamp_rally.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.SampleModel;
import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.utility.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import database.entities.Sample;
import database.entities.Stamps;

/**
 * これは以下の動作を行うサンプル用のクラスです
 * 1. EventBusライブラリによる自身の登録と解除
 * 2. ButterKnifeによるビューの読み込み・イベントハンドラの紐付け
 * 3. Jacksonライブラリによる、Json文字列のクラスへの変換
 */
public class SampleActivity extends AppCompatActivity {
    private final SampleModel model = SampleModel.getInstance();

    //ButterKnifeライブラリによるビューの読み込み
    @InjectView(R.id.sampleTextView)
    TextView sampleView;
    @InjectView(R.id.sampleImageView)
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        //ButterKnifeライブラリを使用する時はこのメソッドを初めに呼ぶ
        ButterKnife.inject(this);
        //EventBusライブラリによる自身の登録
        EventBusUtil.defaultBus.register(this);

        model.fetchJson();
    }

    @Override
    public void onStop() {
        //EventBusライブラリによる自身の登録解除
        EventBusUtil.defaultBus.unregister(this);
        super.onStop();
    }

    //EventBusライブラリによるイベントの登録
    //FetchJsonEventオブジェクトを引数にpostメソッドを呼ぶと、このメソッドが呼ばれる
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Toast.makeText(this, "通信に失敗しました。", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //Json文字列をSampleオブジェクトに変換
            Stamps entity = new ObjectMapper().readValue(event.getJson(), Stamps.class);
            byte[] image = entity.getPicture();
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            iv.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ButterKnifeによるイベント処理
    @OnClick(R.id.button)
    void changeTextView() {
        sampleView.setText("updated");
    }
}
