package com.om1.stamp_rally.controller;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.om1.stamp_rally.R;
import com.om1.stamp_rally.utility.EventBusUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TakeStampActivity extends AppCompatActivity {
    private LocationManager mLocationManager;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //スタンプ獲得時ではなく、スタンプ登録時には位置情報を取得する
        if(getIntent().getBooleanExtra("stampRegisterFlag", false) == true){
            requestLocationUpdates();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        ButterKnife.inject(this);
    }

    private void requestLocationUpdates(){
        //位置情報を取得してインテントに保存する処理
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                3,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        mLocationManager.removeUpdates(this);
                    }
                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                    @Override
                    public void onProviderEnabled(String provider) {
                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                }
        );
    }

    @OnClick(R.id.stamp_button)
    void pushStampButton(){
        Intent intent = getIntent();
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        EventBusUtil.defaultBus.post(getIntent());
    }
}
