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
import android.util.Log;
import android.widget.TextView;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.om1.stamp_rally.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SampleGPSActivity extends AppCompatActivity{
    private LocationManager mLocationManager;
    private Location location;
    private final String providerName = LocationManager.GPS_PROVIDER;
    private final EventBus eventBus = EventBus.getDefault();


    @InjectView(R.id.latitudeTextView)
    TextView latitudeTextView;
    @InjectView(R.id.longitudeTextView)
    TextView longitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_gps);
        ButterKnife.inject(this);
        eventBus.register(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(mLocationManager.isProviderEnabled(providerName)){
            mLocationManager.addTestProvider(providerName, true, true, true, true, true, true, true, Criteria.POWER_HIGH, Criteria.ACCURACY_HIGH);
        }

        location = new Location(providerName);
        location.setAccuracy(Criteria.ACCURACY_HIGH);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(System.currentTimeMillis());

        connectMockLocation();
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    private void connectMockLocation(){
        new Thread(){
            @Override
            public void run(){
                try {
                    new WebSocketFactory().createSocket("ws://10.0.2.2:8080/stamp-rally/location", 5000)
                            .connect()
                            .addListener(new WebSocketAdapter() {
                                @Override
                                public void onTextMessage(WebSocket websocket, String message)
                                        throws Exception {
                                    Log.d("onTextMessage", message);
                                    String[] location = message.split(",", 0);
                                    double latitude = Double.valueOf(location[0]);
                                    double longitude = Double.valueOf(location[1]);
                                    eventBus.post(new LocationEvent(latitude, longitude));
                                }
                            });
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (WebSocketException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void updateGps(LocationEvent event){
        Log.d("GPS", "updateGps");
        location.setLatitude(event.getLatitude());
        location.setLongitude(event.getLongitude());
        mLocationManager.setTestProviderLocation(providerName, location);

        mLocationManager.requestLocationUpdates(
                providerName,
                1000,
                3,
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

    private class LocationEvent{
        private double latitude;
        private double longitude;
        public LocationEvent(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public double getLatitude(){
            return latitude;
        }
        public double getLongitude(){
            return longitude;
        }
    }
}
