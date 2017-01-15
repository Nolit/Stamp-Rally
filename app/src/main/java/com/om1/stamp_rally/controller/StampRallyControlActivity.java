package com.om1.stamp_rally.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    private final String DIALOG_TITLE = "アップロードしますか";
    private final String OK_BUTTON_MESSAGE = "アップロード";
    private final String NO_BUTTON_MESSAGE = "キャンセル";
    private final String ERROR_MESSAGE = "名称を入力してください";
    private final String UPLOAD_SUCCESS_MESSAGE = "アップロードしました";
    private final String UPLOAD_FAILE_MESSAGE = "アップロードに失敗しました\n編集画面からもう一度お試しください";
    private final String RALLY_COMPLETE_MESSAGE = "クリアしました！";
    private final String ATTENTION_STAMP_SAVE_MESSAGE = "スタンプを保存してください！";
    private final float OVERLAY_ALPHA = 0.7f;

    private String title;
    private String note;

    EditText titleEdit;
    TextView stampTitleError;
    EditText noteEdit;

    private int selectedItemIndex;
    private List<StampRallys> stampDataList;
    private StampRallyEditListAdapter adapter;
    private List<Map<String, Object>> stampRallyMapList;


    //ここから追加分（大脇）
    Button stampRallyNewCreateButton;
    Button stampRallyDeleteButton;

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
        stampRallyDeleteButton = (Button) findViewById(R.id.deleteButton);
        //ここまで追加分（大脇）
    }

    @Override
    public void onResume(){
        super.onResume();
        stampDataList = loadStampRallyData();
        adapter = new StampRallyEditListAdapter(this, 0, stampDataList);

        ListView listView = (ListView)findViewById(R.id.stampRallyCreatingListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                Stamps stamp = (Stamps) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), stamp.getStampName() + " clicked",
                        Toast.LENGTH_LONG).show();

                final View layout = StampRallyControlActivity.this.getLayoutInflater().inflate(R.layout.stamp_info,
                        (ViewGroup)findViewById(R.id.layout_root));

                selectedItemIndex = position;
            }
        });
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
        Integer id = (Integer) stampRallyMap.get("stampRallyId");
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
