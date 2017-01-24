package com.om1.stamp_rally.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.adapter.StampRallyEditListAdapter;
import com.om1.stamp_rally.utility.dbadapter.StampRallyDbAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import database.entities.StampRallys;
import database.entities.Stamps;

import static butterknife.ButterKnife.findById;

public class StampRallyControlActivity extends AppCompatActivity {
    private List<StampRallys> stampDataList;
    private StampRallyEditListAdapter adapter;
    private List<Map<String, Object>> stampRallyMapList;
    private ListView listView;

    EditText titleEdit;
    EditText summaryEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_control);
        ButterKnife.inject(this);

        listView = (ListView)findViewById(R.id.stampRallyCreatingListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(StampRallyControlActivity.this, StampRallyCreateActivity.class);
                i.putExtra("stampRallyId", stampDataList.get(position).getStamprallyId());
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        stampDataList = loadStampRallyData();
        adapter = new StampRallyEditListAdapter(this, 0, stampDataList);
        listView.setAdapter(adapter);
    }

    private List<StampRallys> loadStampRallyData(){
        stampRallyMapList = new StampRallyDbAdapter(this).getAllAsList();
        List<StampRallys> stampRallyList = new ArrayList<>();
        for(Map<String, Object> stampRallyMap : stampRallyMapList){
            stampRallyList.add(convertMapToStampRally(stampRallyMap));
        }

        return stampRallyList;
    }

    private StampRallys convertMapToStampRally(Map<String, Object> stampRallyMap){
        Integer id = (Integer) stampRallyMap.get("id");
        String name = (String) stampRallyMap.get("name");
        String summary = (String) stampRallyMap.get("summary");

        StampRallys stampRally = new StampRallys();
        stampRally.setStamprallyId(id);
        stampRally.setStamprallyName(name);
        stampRally.setStamrallyComment(summary);

        return stampRally;
    }

    @OnClick(R.id.stampRallyNewCreateButton)
    public void createStampRally(){
        final View layout = getLayoutInflater().inflate(R.layout.stamp_rally_info,
                (ViewGroup)findViewById(R.id.stamp_rally_dialog_content));

        initDialogViews(layout);
        showCreateStampRallyDialog(layout);
    }

    private void initDialogViews(View layout){
        titleEdit = findById(layout, R.id.stampRallyTitleEdit);
        summaryEdit = findById(layout, R.id.stampRallySummaryEdit);
    }

    private void showCreateStampRallyDialog(final View layout){
        new AlertDialog.Builder(this)
                .setTitle("スタンプラリー新規作成")
                .setView(layout)
                .setPositiveButton("作成", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String title = titleEdit.getText().toString();
                        String summary = summaryEdit.getText().toString();
                        new StampRallyDbAdapter(StampRallyControlActivity.this).createStampRally(title, summary);
                        Intent intent=new Intent();
                        intent.setClass(StampRallyControlActivity.this, StampRallyControlActivity.this.getClass());
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("取り消し", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false)
                .create().show();
    }
}
