package com.om1.stamp_rally.controller;

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

import database.entities.StampRallys;
import database.entities.Stamps;

import static butterknife.ButterKnife.findById;

public class StampRallyControlActivity extends AppCompatActivity {
    private List<StampRallys> stampDataList;
    private StampRallyEditListAdapter adapter;
    private List<Map<String, Object>> stampRallyMapList;
    private ListView listView;

    //ここから追加分（大脇）
    Button stampRallyNewCreateButton;

    /*
        関連ソース
        ・activity_stamprally_control.xmll
        ・list_row_create_stamprally.xml
        ・CreateStampRallyBean.java
        ・CreateStampRallyListAdapter.java

        xmlとButtonのfindView関連付けだけ修正済み
        それ以外はStampEditListActivityをコピーしてそのまま残してる
     */
    //ここまで追加分（大脇）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamprally_control);

        //ここから追加分（大脇）
        stampRallyNewCreateButton = (Button) findViewById(R.id.stampRallyNewCreateButton);
        stampRallyNewCreateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(StampRallyControlActivity.this, StampRallyCreateActivity.class);
                startActivity(intent);
            }
        });
        //ここまで追加分（大脇）

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

    @Override
    public void onStop() {
        super.onStop();
    }
}
