package com.om1.stamp_rally.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampUpload;
import com.om1.stamp_rally.model.adapter.StampEditListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.om1.stamp_rally.model.event.UploadedStampEvent;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.dbadapter.StampDbAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import database.entities.StampPads;
import database.entities.StampRallys;
import database.entities.Stamps;

import static butterknife.ButterKnife.findById;

public class StampEditListActivity extends AppCompatActivity {
    private final String DIALOG_TITLE = "アップロードしますか";
    private final String OK_BUTTON_MESSAGE = "アップロード";
    private final String NO_BUTTON_MESSAGE = "キャンセル";
    private final String ERROR_MESSAGE = "名称を入力してください";
    private final String UPLOAD_SUCCESS_MESSAGE = "アップロードしました";
    private final String UPLOAD_FAILE_MESSAGE = "アップロードに失敗しました\n編集画面からもう一度お試しください";
    private final String ATTENTION_STAMP_UPLOAD_MESSAGE = "少々お待ちください";
    private final float OVERLAY_ALPHA = 0.7f;

    EditText titleEdit;
    TextView stampTitleError;
    EditText noteEdit;

    private boolean canBackPressed = true;

    private int selectedItemIndex;
    private List<Stamps> stampDataList;
    private StampEditListAdapter adapter;
    private List<Map<String, Object>> stampMapList;
    private Integer stampRallyId;
    private String title;
    private String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_edit_list);

        stampDataList = loadStampData();
        adapter = new StampEditListAdapter(this, 0, stampDataList);

        ListView listView = (ListView)findViewById(R.id.stampEditlistView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemIndex = position;
                ListView listView = (ListView) parent;
                Stamps stamp = (Stamps) listView.getItemAtPosition(position);
                final View layout = StampEditListActivity.this.getLayoutInflater().inflate(R.layout.stamp_info,
                        (ViewGroup)findViewById(R.id.layout_root));

                initDialogViews(layout, stamp);
                showEditStampDialog(layout, stamp);
            }
        });
    }

    private List<Stamps> loadStampData(){
        stampMapList = new StampDbAdapter(this).getAllAsList();
        List<Stamps> stampList = new ArrayList<>();
        for(Map<String, Object> stampMap : stampMapList){
            Stamps stampData = convertMapToStamp(stampMap);
            stampList.add(stampData);
        }

        return stampList;
    }

    private Stamps convertMapToStamp(Map<String, Object> stampMap){
        Integer id = (Integer) stampMap.get("stampId");
        String title = (String) stampMap.get("title");
        String memo = (String) stampMap.get("memo");
        String stampRallyName = (String) stampMap.get("stampRallyName");

        byte[] picture = (byte[]) stampMap.get("picture");

        Integer stampRallyId = (Integer)stampMap.get("stampRallyId");
        StampRallys stampRally = new StampRallys();
        stampRally.setStamprallyId(stampRallyId);
        stampRally.setStamprallyName(stampRallyName);

        StampPads pad = new StampPads();
        pad.setLatitude((Double)stampMap.get("latitude"));
        pad.setLongitude((Double)stampMap.get("longitude"));

        Stamps stamp = new Stamps();
        stamp.setStampId(id);
        stamp.setStampName(title);
        stamp.setStampComment(memo);
        stamp.setPicture(picture);
        stamp.setStampDate(new Date(((Long)stampMap.get("create_time"))));
        stamp.getStampRallysList().add(stampRally);
        stamp.setStampPads(pad);

        return stamp;
    }

    private void initDialogViews(View layout, Stamps stamp){
        titleEdit = findById(layout, R.id.stampTitleEdit);
        titleEdit.setText(stamp.getStampName());
        noteEdit = findById(layout, R.id.stampNoteEdit);
        noteEdit.setText(stamp.getStampComment());
        stampTitleError = findById(layout, R.id.stampTitleError);
    }

    private void showEditStampDialog(final View layout, final Stamps stamp){
        new AlertDialog.Builder(this)
            .setTitle(DIALOG_TITLE)
            .setView(layout)
            .setPositiveButton(OK_BUTTON_MESSAGE, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String title = titleEdit.getText().toString();
                    if(title.equals("")){
                        stampTitleError.setText(ERROR_MESSAGE);
                        return;
                    }
                    applyDialogEditField();
                    uploadStamp(stamp);
                }
            })
            .setNegativeButton(NO_BUTTON_MESSAGE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .setCancelable(false)
            .create().show();
    }

    private void applyDialogEditField(){
        title = titleEdit.getText().toString();
        note = noteEdit.getText().toString();
    }

    private void showOverlay(){
        canBackPressed = false;
        FrameLayout overlayLayout = findById(this, R.id.uploading_overlay);
        overlayLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        overlayLayout.setAlpha(OVERLAY_ALPHA);
    }

    private void hideOverlay(){
        canBackPressed = true;
        FrameLayout overlayLayout = findById(this, R.id.uploading_overlay);
        overlayLayout.setOnTouchListener(null);
        overlayLayout.setAlpha(0);
    }

    private void uploadStamp(Stamps stamp){
        showOverlay();

        Integer id = stamp.getStampId();
        stampRallyId = stamp.getStampRallysList().get(0).getStamprallyId();
        double latitude = stamp.getStampPads().getLatitude();
        double longitude = stamp.getStampPads().getLongitude();
        byte[] picture = stamp.getPicture();
        long createTime = stamp.getStampDate().getTime();

        SharedPreferences pref = getSharedPreferences("stamp-rally", MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
        String mailAddress = pref.getString("mailAddress", "tarou2");
        String password = pref.getString("password", "tarou2");

        StampUpload.getInstance().uploadStamp(id, stampRallyId, latitude, longitude, title, note, picture, createTime, mailAddress, password);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadedStamp(UploadedStampEvent event) {
        String message;
        if(event.isSuccess()){
            int id = (int)stampMapList.get(selectedItemIndex).get("id");
            new StampDbAdapter(this).deleteById(id);

            if(event.isClear()){
                Intent intent = new Intent(this, ClearActivity.class);
                intent.putExtra("stampRallyId", stampRallyId);
                startActivity(new Intent(this, ClearActivity.class));
            }
            message = UPLOAD_SUCCESS_MESSAGE;
        }else{
            message =UPLOAD_FAILE_MESSAGE;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        stampDataList.remove(selectedItemIndex);
        adapter.notifyDataSetChanged();
        hideOverlay();
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBusUtil.defaultBus.register(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        EventBusUtil.defaultBus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        if(!canBackPressed){
            Toast.makeText(this, ATTENTION_STAMP_UPLOAD_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }
}
