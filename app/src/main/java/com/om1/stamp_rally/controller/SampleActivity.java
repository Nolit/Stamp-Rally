package com.om1.stamp_rally.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.SampleModel;
import com.om1.stamp_rally.model.event.FetchJsonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import database.entities.Sample;

/**
 * これは以下の動作を行うサンプル用のクラスです
 * 1. EventBusライブラリによる自身の登録と解除
 * 2. ButterKnifeによるビューの読み込み・イベントハンドラの紐付け
 * 3. Jacksonライブラリによる、Json文字列のクラスへの変換
 */
public class SampleActivity extends AppCompatActivity {
    private final EventBus eventBus = EventBus.getDefault();
    private final SampleModel model = SampleModel.getInstance();

    //ButterKnifeライブラリによるビューの読み込み
    @InjectView(R.id.sampleTextView)
    TextView sampleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        //ButterKnifeライブラリを使用する時はこのメソッドを初めに呼ぶ
        ButterKnife.inject(this);
        //EventBusライブラリによる自身の登録
        eventBus.register(this);

        model.fetchJson();
    }

    @Override
    public void onStop() {
        //EventBusライブラリによる自身の登録解除
        eventBus.unregister(this);
        super.onPause();
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
            Sample entity = new ObjectMapper().readValue(event.getJson(), Sample.class);
            sampleView.setText("name=" + entity.getName() + "\n" +
                                "email=" + entity.getEmail());
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
