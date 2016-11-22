package com.om1.stamp_rally.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.om1.stamp_rally.R;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SampleGPSActivity extends AppCompatActivity{
    private LocationManager mLocationManager;
    private Location location;
    private final String providerName = LocationManager.GPS_PROVIDER;

    @InjectView(R.id.latitudeTextView)
    TextView latitudeTextView;
    @InjectView(R.id.longitudeTextView)
    TextView longitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_gps);
        ButterKnife.inject(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(mLocationManager.isProviderEnabled(providerName)){
            mLocationManager.addTestProvider(providerName, true, true, true, true, true, true, true, Criteria.POWER_HIGH, Criteria.ACCURACY_HIGH);
        }

        location = new Location(providerName);
        location.setAccuracy(Criteria.ACCURACY_HIGH);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(System.currentTimeMillis());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.GpsUpdateButton)
    void updateGps(){
        Random rnd = new Random();
        location.setLatitude(rnd.nextDouble());
        location.setLongitude(rnd.nextDouble());
        mLocationManager.setTestProviderLocation(providerName, location);
        mLocationManager.requestLocationUpdates(
                providerName, //LocationManager.GPS_PROVIDER, //LocationManager.NETWORK_PROVIDER,
                1000, // 通知のための最小時間間隔（ミリ秒）
                3, // 通知のための最小距離間隔（メートル）
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (ActivityCompat.checkSelfPermission(SampleGPSActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SampleGPSActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        latitudeTextView.setText(String.valueOf(location.getLatitude()));
                        longitudeTextView.setText(String.valueOf(location.getLongitude()));
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
}
