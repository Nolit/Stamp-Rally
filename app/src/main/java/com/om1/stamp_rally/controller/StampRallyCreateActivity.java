package com.om1.stamp_rally.controller;

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
        if(!isNewCreate()){
            stampRallyData = new StampRallyDbAdapter(this).getById(getIntent().getIntExtra("stampRallyId", -1));
            editTitle.setText((String) stampRallyData.get("name"));
            editComment.setText((String) stampRallyData.get("summary"));
        }
    }

    @OnClick(R.id.saveStampRallyButton)
    public void saveStampRally(){
        if(isNewCreate()){
            new StampRallyDbAdapter(this).createStampRally(editTitle.getText().toString(), editComment.getText().toString());
        }else{
            stampRallyData.put("name", editTitle.getText().toString());
            stampRallyData.put("summary", editComment.getText().toString());
            new StampRallyDbAdapter(this).update(stampRallyData);
        }
        finish();
    }

    private boolean isNewCreate(){
        return getIntent().getIntExtra("stampRallyId", -1) == -1;
    }

    @OnClick(R.id.backButton)
    public void backActivity(){
        finish();
    }
}
