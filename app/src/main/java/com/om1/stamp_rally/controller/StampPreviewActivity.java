package com.om1.stamp_rally.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampUpload;
import com.om1.stamp_rally.model.event.UploadedStampEvent;
import com.om1.stamp_rally.utility.ByteConverter;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.dbadapter.StampDbAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;
import static butterknife.ButterKnife.findById;
import butterknife.OnClick;

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
    private final String UPLOAD_SUCCESS_MESSAGE = "アップロードしました";
    private final String UPLOAD_FAILE_MESSAGE = "アップロードに失敗しました\n編集画面からもう一度お試しください";
    private final String RALLY_COMPLETE_MESSAGE = "クリアしました！";
    private final String ATTENTION_STAMP_SAVE_MESSAGE = "スタンプを保存してください！";
    private final float OVERLAY_ALPHA = 0.7f;

    private int stampId;
    private int stampRallyId;
    private String stampRallyName;
    private double latitude;
    private double longitude;
    private String title;
    private String note;
    private byte[] picture;

    //trueの時、戻るボタンを無効化にする
    private boolean decidePictureFlag = false;
    private static int PICTURE_MAX_SIZE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.inject(this);

        stampId = getIntent().getIntExtra("stampId", 0);
        stampRallyId = getIntent().getIntExtra("stampRallyId", 0);
        stampRallyName = getIntent().getStringExtra("stampRallyName");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        setupCropImageView();
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

    private void setupCropImageView(){
        cropImageView.setOutputMaxSize(PICTURE_MAX_SIZE, PICTURE_MAX_SIZE);
        cropImageView.setCompressFormat(Bitmap.CompressFormat.JPEG);
        Bitmap pictureImage = changeDisplayOrientation(getIntent().getByteArrayExtra("pictureImage"));
        cropImageView.setImageBitmap(pictureImage);
    }

    private Bitmap changeDisplayOrientation(byte[] image){
        Bitmap origin_bitmap = BitmapFactory.decodeByteArray (image, 0, image.length);
        int width = origin_bitmap.getWidth ();
        int height = origin_bitmap.getHeight ();
        Matrix matrix = new Matrix();
        matrix.postRotate (90);
        return Bitmap.createBitmap (origin_bitmap, 0, 0, width, height, matrix, true);
    }

    @OnClick(R.id.takeAgainButton)
    void backActivity(){
        finish();
    }

    @OnClick(R.id.decideButton)
    void decidePicture(){
        decidePictureFlag = true;
        cropPicture();
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(
                LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.stamp_info,
                (ViewGroup)findViewById(R.id.layout_root));

        initDialogViews(layout);
        showEditStampDialog(layout);
    }

    private void cropPicture(){
        cropImageView.startCrop(null, new CropCallback() {
            @Override
            public void onSuccess(Bitmap cropped) {
                picture = ByteConverter.convert(cropped);
            }
            @Override
            public void onError() {}
        }, null);
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
        showOverlay();
        long createTime = System.currentTimeMillis();
        SharedPreferences pref = getSharedPreferences("main", MODE_PRIVATE);
        String mailAddress = pref.getString("mailAddress", "tarou2");
        String password = pref.getString("password", "tarou2");
        StampUpload.getInstance().uploadStamp(stampId, stampRallyId, latitude, longitude, title, note, picture, createTime, mailAddress, password);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadedStamp(UploadedStampEvent event) {
        String message;
        if(event.isSuccess()){
            if(event.isClear()){
                Intent intent = new Intent(this, ClearActivity.class);
                intent.putExtra("stampRallyId", stampRallyId);
                startActivity(intent);
                return;
            }
            message = event.isClear() ? RALLY_COMPLETE_MESSAGE : UPLOAD_SUCCESS_MESSAGE;
        }else{
            message =UPLOAD_FAILE_MESSAGE;
            saveStamp();
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        startActivity(new Intent(StampPreviewActivity.this, MainActivity.class));
    }

    private void saveStamp(){
        new StampDbAdapter(this).createStamp(stampId, stampRallyId, stampRallyName, title, note, picture, latitude, longitude);
    }

    private void showOverlay(){
        FrameLayout overlayLayout = findById(this, R.id.uploading_overlay);
        overlayLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        overlayLayout.setAlpha(OVERLAY_ALPHA);
    }

    @Override
    public void onBackPressed() {
        if(decidePictureFlag){
            Toast.makeText(this, ATTENTION_STAMP_SAVE_MESSAGE, Toast.LENGTH_LONG).show();
            return;
        }
        super.onBackPressed();
    }
}
