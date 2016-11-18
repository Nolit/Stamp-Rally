package com.om1.stamp_rally.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.SampleAsyncTask;
import com.om1.stamp_rally.model.SampleModel;
import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import database.entities.Sample;
import static java.lang.System.out;


public class MainActivity extends AppCompatActivity {
    private final EventBus eventBus = EventBus.getDefault();
    private final SampleModel model = SampleModel.getInstance();

    @InjectView(R.id.sampleTextView)
    TextView sampleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        eventBus.register(this);
        model.connect();
//        new SampleAsyncTask(sampleView).execute();
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void fetchedJson(FetchJsonEvent event) {
        if (event.isSuccess()) {
            Log.d("TAG", "My model async task is success");
            out.println(event.getJson());
        } else {
            Log.d("TAG", "My model async task is failure");
        }
    }

    @OnClick(R.id.button)
    void changeTextView() {
        sampleView.setText("updated");
    }
}
