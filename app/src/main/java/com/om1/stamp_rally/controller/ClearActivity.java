package com.om1.stamp_rally.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampRallyClear;
import com.om1.stamp_rally.utility.dbadapter.StampDbAdapter;
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClearActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        ButterKnife.inject(this);
    }
}
