package com.om1.stamp_rally.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.adapter.StampEditListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.om1.stamp_rally.utility.dbadapter.StampDbAdapter;
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;

import database.entities.StampRallys;
import database.entities.Stamps;

public class StampEditListActivity extends AppCompatActivity {
    private final String DIALOG_TITLE = "アップロードしますか";
    private final String OK_BUTTON_MESSAGE = "アップロード";
    private final String NO_BUTTON_MESSAGE = "キャンセル";
    private final String ERROR_MESSAGE = "名称を入力してください";
    private final String UPLOAD_SUCCESS_MESSAGE = "アップロードしました";
    private final String UPLOAD_FAILE_MESSAGE = "アップロードに失敗しました\n編集画面からもう一度お試しください";
    private final String RALLY_COMPLETE_MESSAGE = "クリアしました！";
    private final String ATTENTION_STAMP_SAVE_MESSAGE = "スタンプを保存してください！";

    private StampEditListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_edit_list);

        List<Stamps> stampDataList = loadStampData();
        adapter = new StampEditListAdapter(this, 0, stampDataList);

        ListView listView = (ListView)findViewById(R.id.stampEditlistView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                Stamps stamp = (Stamps) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), stamp.getStampName() + " clicked",
                        Toast.LENGTH_LONG).show();


            }
        });

    }

    private List<Stamps> loadStampData(){
        List<Map<String, Object>> stampMapList = new StampDbAdapter(this).getAllAsList();
        Log.d("スタンプラリー", stampMapList.toString());
        List<Stamps> stampList = new ArrayList<>();
        for(Map<String, Object> stampMap : stampMapList){
            Stamps stampData = convertMapToStamp(stampMap);
            stampList.add(stampData);
        }

        return stampList;
    }

    private Stamps convertMapToStamp(Map<String, Object> stampMap){
        Integer id = (Integer) stampMap.get("id");
        String title = (String) stampMap.get("title");
        String memo = (String) stampMap.get("memo");
        byte[] picture = (byte[]) stampMap.get("picture");

        Integer stampRallyId = (Integer)stampMap.get("stampRallyId");
        StampRallys stampRally = new StampRallys();
        stampRally.setStamprallyId(stampRallyId);
        if(stampRallyId != 0){
            String name = (String)new StampRallyDbAdapter(this).getById(stampRallyId).get("name");
            stampRally.setStamprallyName(name);
        }

        Stamps stamp = new Stamps();
        stamp.setStampId(id);
        stamp.setStampName(title);
        stamp.setStampComment(memo);
        stamp.setPicture(picture);
        stamp.getStampRallysList().add(stampRally);

        return stamp;
    }

//    private void showEditStampDialog(final View layout){
//        new AlertDialog.Builder(this)
//                .setTitle(DIALOG_TITLE)
//                .setView(layout)
//                .setPositiveButton(OK_BUTTON_MESSAGE, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        String title = titleEdit.getText().toString();
//                        if(title.equals("")){
//                            stampTitleError.setText(ERROR_MESSAGE);
//                            return;
//                        }
//                        applyDialogEditField();
//                        uploadStamp();
//                    }
//                })
//                .setNegativeButton(NO_BUTTON_MESSAGE, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        applyDialogEditField();
//                        saveStamp();
//                        startActivity(new Intent(StampPreviewActivity.this, MainActivity.class));
//                    }
//                })
//                .setCancelable(false)
//                .create().show();
//    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
