package com.om1.stamp_rally.controller.view;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.om1.stamp_rally.controller.StampPreviewActivity;
import com.om1.stamp_rally.model.event.StampEvent;
import com.om1.stamp_rally.utility.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by yaboo on 2016/12/05.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private final int DISPLAY_ORIENTATIOM = 90;
    private Camera camera;


    public CameraView(Context context, AttributeSet attrs) {
        super(context);
        SurfaceHolder holder=getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            EventBusUtil.defaultBus.register(this);

            camera=Camera.open();
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(DISPLAY_ORIENTATIOM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int w,int h) {
        setPreview(w, h);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera=null;

        EventBusUtil.defaultBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void takePicture(final StampEvent event) {
        Camera.ShutterCallback shutterListener = new Camera.ShutterCallback() {
            public void onShutter() {
            }
        };
        camera.takePicture(shutterListener, null,new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data,Camera camera) {
                Intent intent = new Intent(getContext(), StampPreviewActivity.class);
                intent.putExtra("pictureImage", data);
                intent.putExtra("stampId", event.getStampId());
                intent.putExtra("stampRallyId", event.getStampRallyId());
                intent.putExtra("latitude", event.getLatitude());
                intent.putExtra("longitude", event.getLongitude());

                getContext().startActivity(intent);
            }
        });
    }

    private void setPreview(int width, int height){
        Camera.Parameters params = camera.getParameters();
        Camera.Size size = params.getSupportedPreviewSizes().get(0);
        params.setPreviewSize(size.width, size.height);
        camera.setParameters(params);
    }
}