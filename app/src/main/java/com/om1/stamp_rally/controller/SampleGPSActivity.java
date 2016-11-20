package com.om1.stamp_rally.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.om1.stamp_rally.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SampleGPSActivity extends AppCompatActivity {
    private LocationManager mLocationManager;

    @InjectView(R.id.latitudeTextView)
    TextView latitudeTextView;
    @InjectView(R.id.longitudeTextView)
    TextView longitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_gps);
        ButterKnife.inject(this);

        // GPS
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsFlg = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d("GPS Enabled", gpsFlg ? "OK" : "NG");

        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, //LocationManager.NETWORK_PROVIDER,
                1000, // 通知のための最小時間間隔（ミリ秒）
                3, // 通知のための最小距離間隔（メートル）
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Toast.makeText(SampleGPSActivity.this, "onLocationChangedが呼ばれたよ！！。", Toast.LENGTH_LONG).show();
                        if (ActivityCompat.checkSelfPermission(SampleGPSActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SampleGPSActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(SampleGPSActivity.this, "しかしPermissionエラー・・・。", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(SampleGPSActivity.this, "TextViewを更新するよ！！", Toast.LENGTH_LONG).show();
                        latitudeTextView.setText(String.valueOf(location.getLatitude()));
                        longitudeTextView.setText(String.valueOf(location.getLongitude()));

//                    mLocationManager.removeUpdates(this);
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
}
