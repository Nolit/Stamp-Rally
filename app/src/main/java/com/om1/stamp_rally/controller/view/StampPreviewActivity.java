package com.om1.stamp_rally.controller.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.GetStampModel;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import static butterknife.ButterKnife.findById;

public class StampPreviewActivity extends AppCompatActivity {
    EditText titleEdit;
    TextView stampTitleError;
    EditText noteEdit;
    @InjectView(R.id.cropImageView)
    CropImageView cropImageView;

    private final String DIALOG_TITLE = "スタンプ獲得！！";
    private final String OK_BUTTON_MESSAGE = "スタンプを押す";
    private final String NO_BUTTON_MESSAGE = "戻る";
    private final String ERROR_MESSAGE = "名称を入力してください";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.inject(this);


        Bitmap pictureImage = changeDisplayOrientation(getIntent().getByteArrayExtra("pictureImage"));
        cropImageView.setImageBitmap(pictureImage);
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
    void nextActivity(){
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

    private void showEditStampDialog(View layout){
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
            saveStamp();
            }
        })
        .setNegativeButton(NO_BUTTON_MESSAGE, null)
        .setCancelable(false).create().show();
    }

    private void saveStamp(){
        Bitmap bm = cropImageView.getCroppedBitmap();
        byte[]  stampPicture = convert(bm);
        String title = titleEdit.getText().toString();
        String note = noteEdit.getText().toString();

        GetStampModel.getInstance().postGotStamp(title, note, stampPicture);
    }

    private byte[] convert(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
