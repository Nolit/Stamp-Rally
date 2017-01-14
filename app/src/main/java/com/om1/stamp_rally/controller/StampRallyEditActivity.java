package com.om1.stamp_rally.controller;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampRallyDetailModel;
import com.om1.stamp_rally.model.event.FetchJsonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import database.entities.StampRallys;
import database.entities.Stamps;

public class StampRallyEditActivity extends AppCompatActivity {
    SharedPreferences mainPref;

    EditText editTitle;
    EditText editComment;
    Button saveStampRallyButton;
    Button uploadStampRallyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_edit);
        editTitle = (EditText) findViewById(R.id.editStampRallyTitle);
        editComment = (EditText) findViewById(R.id.editStampRallyComment);
        saveStampRallyButton = (Button) findViewById(R.id.saveStampRallyButton);
        uploadStampRallyButton = (Button) findViewById(R.id.uploadButton);

    }

}
