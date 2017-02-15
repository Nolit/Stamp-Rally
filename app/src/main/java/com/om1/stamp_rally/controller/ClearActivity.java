package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.om1.stamp_rally.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClearActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.returnTopButton)
    public void returnBack(){
        startActivity(new Intent(this, MainActivity.class));
    }
}
