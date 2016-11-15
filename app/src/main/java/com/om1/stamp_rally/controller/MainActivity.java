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

import butterknife.ButterKnife;
import butterknife.InjectView;
import database.entities.Sample;


public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.sampleTextView)
    TextView sampleView;
    @InjectView(R.id.button)
    Button sampleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        sampleView.setText("Default2");
        sampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleView.setText("updated");
            }
        });
//        new SampleAsyncTask(sampleView).execute();
    }

}
