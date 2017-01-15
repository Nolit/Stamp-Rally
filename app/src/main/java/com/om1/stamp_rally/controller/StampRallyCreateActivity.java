package com.om1.stamp_rally.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StampRallyCreateActivity extends AppCompatActivity {
    EditText editTitle;
    EditText editComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_create);
        ButterKnife.inject(this);

        editTitle = (EditText) findViewById(R.id.editStampRallyTitle);
        editComment = (EditText) findViewById(R.id.editStampRallyComment);
    }

    @OnClick(R.id.saveStampRallyButton)
    public void saveStampRally(){
        new StampRallyDbAdapter(this).createStampRally(editTitle.getText().toString(), editComment.getText().toString());
        finish();
    }

    @OnClick(R.id.backButton)
    public void backActivity(){
        finish();
    }
}
