package com.om1.stamp_rally;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Request;

import database.entities.Sample;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ObjectMapper mapper = new ObjectMapper();

         new Request.Builder()
                .url("http://stamp-rally/sample");
        new Sample();

    }

}
