package com.om1.stamp_rally.controller;

import android.content.Context;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.inject(this);

        //スタンプ取得時ではなくスタンプ登録時の処理
        if(getIntent().getBooleanExtra("stampRegisterFlag", false) == true){
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates(){
        //位置情報を取得してインテントに保存する処理
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, //LocationManager.NETWORK_PROVIDER,
                3000, // 通知のための最小時間間隔（ミリ秒）
                10, // 通知のための最小距離間隔（メートル）
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d("スタンプラリー", "位置情報を取得");
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
        EventBusUtil.defaultBus.post(getIntent());
    }
}
