package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StampRallyCreateActivity extends AppCompatActivity {
    EditText editTitle;
    EditText editComment;
    Map<String, Object> stampRallyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_create);
        ButterKnife.inject(this);

        editTitle = (EditText) findViewById(R.id.editStampRallyTitle);
        editComment = (EditText) findViewById(R.id.editStampRallyComment);
        stampRallyData = new StampRallyDbAdapter(this).getById(getIntent().getIntExtra("stampRallyId", -1));
        editTitle.setText((String) stampRallyData.get("name"));
        editComment.setText((String) stampRallyData.get("summary"));
    }

    @OnClick(R.id.saveStampRallyButton)
    public void saveStampRally(){
        stampRallyData.put("name", editTitle.getText().toString());
        stampRallyData.put("summary", editComment.getText().toString());
        new StampRallyDbAdapter(this).update(stampRallyData);
        finish();
    }

    @OnClick(R.id.addStampButton)
    public void addStamp(){
        Intent i = new Intent(StampRallyCreateActivity.this, SpecifyStampRallyStructure.class);
        i.putExtra("stampRallyId", getIntent().getIntExtra("stampRallyId", -1));
        startActivity(i);
    }

    @OnClick(R.id.backButton)
    public void backActivity(){
        finish();
    }
}
