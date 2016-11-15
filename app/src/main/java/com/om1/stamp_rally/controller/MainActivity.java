package com.om1.stamp_rally.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.SampleAsyncTask;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import database.entities.Sample;


public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.sampleTextView)
    TextView sampleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        new SampleAsyncTask(sampleView).execute();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @OnClick(R.id.button)
    void changeTextView() {
        sampleView.setText("updated");
    }
}
