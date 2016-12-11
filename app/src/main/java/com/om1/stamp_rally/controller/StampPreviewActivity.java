package com.om1.stamp_rally.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isseiaoki.simplecropview.CropImageView;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampUpload;
import com.om1.stamp_rally.model.event.FetchJsonEvent;
import com.om1.stamp_rally.model.event.StampUploadEvent;
import com.om1.stamp_rally.utility.ByteConverter;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.dbadapter.StampDbAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import database.entities.Sample;

import static butterknife.ButterKnife.findById;

public class StampPreviewActivity extends AppCompatActivity {
    EditText titleEdit;
    TextView stampTitleError;
    EditText noteEdit;
    @InjectView(R.id.cropImageView)
    CropImageView cropImageView;

    private final String DIALOG_TITLE = "すぐにアップロードしますか";
    private final String OK_BUTTON_MESSAGE = "アップロード";
    private final String NO_BUTTON_MESSAGE = "後で";
    private final String ERROR_MESSAGE = "名称を入力してください";

    private int stampId;
    private int stampRallyId;
    private double latitude;
    private double longitude;
    private String title;
    private String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.inject(this);
        EventBusUtil.defaultBus.register(this);

        stampId = getIntent().getIntExtra("stampId", 0);
        stampRallyId = getIntent().getIntExtra("stampRallyId", 0);
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        Bitmap pictureImage = changeDisplayOrientation(getIntent().getByteArrayExtra("pictureImage"));
        cropImageView.setImageBitmap(pictureImage);
    }

    @Override
    public void onStop() {
        //EventBusライブラリによる自身の登録解除
        EventBusUtil.defaultBus.unregister(this);
        super.onStop();
    }

    private Bitmap changeDisplayOrientation(byte[] image){
        Bitmap tmp_bitmap = BitmapFactory.decodeByteArray (image, 0, image.length);
        int width = tmp_bitmap.getWidth ();
        int height = tmp_bitmap.getHeight ();
        Matrix matrix = new Matrix();
        matrix.postRotate (90);
        return Bitmap.createBitmap (tmp_bitmap, 0, 0, width, height, matrix, true);
    }

    @OnClick(R.id.takeAgainButton)
    void backActivity(){
        finish();
    }

    @OnClick(R.id.decideButton)
    void decidePicture(){
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(
                LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.stamp_info,
                (ViewGroup)findViewById(R.id.layout_root));

        initDialogViews(layout);
        showEditStampDialog(layout);
    }

    private void initDialogViews(View layout){
        titleEdit = findById(layout, R.id.stampTitleEdit);
        noteEdit = findById(layout, R.id.stampNoteEdit);
        stampTitleError = findById(layout, R.id.stampTitleError);
    }

    private void showEditStampDialog(final View layout){
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
                uploadStamp();
            }
        })
        .setNegativeButton(NO_BUTTON_MESSAGE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                applyDialogEditField();
                saveStamp();
                startActivity(new Intent(StampPreviewActivity.this, MainActivity.class));
            }
        })
        .setCancelable(false)
        .create().show();
    }

    private void applyDialogEditField(){
        title = titleEdit.getText().toString();
        note = noteEdit.getText().toString();
    }

    private void uploadStamp(){
        byte[] picture = ByteConverter.convert(cropImageView.getCroppedBitmap());
        long createTime = System.currentTimeMillis();
        StampUpload.getInstance().uploadStamp(stampId, stampRallyId, latitude, longitude, title, note, picture, createTime);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadedStamp(StampUploadEvent event) {
        if(!event.isSuccess()){
            Log.d("スタンプラリー", "アップロードに失敗");
            return;
        }
        Log.d("スタンプラリー", "アップロードに成功");
        if(event.isClear()){
            Log.d("スタンプラリー", "クリア！");
            return;
        }
        Log.d("スタンプラリー", "ページ遷移します");
    }

    private void saveStamp(){
        byte[] picture = ByteConverter.convert(cropImageView.getCroppedBitmap());
        new StampDbAdapter(this).createStamp(stampId, stampRallyId, title, note, picture, latitude, longitude);
    }
}
