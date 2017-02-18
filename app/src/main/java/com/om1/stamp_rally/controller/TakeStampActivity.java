package com.om1.stamp_rally.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.utility.EventBusUtil;
import com.om1.stamp_rally.utility.Url;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static butterknife.ButterKnife.findById;

public class TakeStampActivity extends AppCompatActivity implements LocationListener {
    private final EventBus eventBus = EventBus.getDefault();

    private LocationManager mLocationManager;
    private double latitude = 34.66372428473802;
    private double longitude = 135.51852397620678;

    //位置情報詐称
    private boolean isMockLocationEnabled = false;
    private Location testLocation;
    private final String providerName = LocationManager.GPS_PROVIDER;
    private WebSocket mockLocationClient;

    @InjectView(R.id.stamp_button)
    ImageButton stampButton;
    @InjectView(R.id.debugText)
    TextView debugText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        ButterKnife.inject(this);
        debugText.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume(){
        super.onResume();
        eventBus.register(this);

        //スタンプ獲得時ではなく、スタンプ登録時には位置情報を取得する
        if(getIntent().getBooleanExtra("stampRegisterFlag", false) == true){
            mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if(isMockLocationEnabled){
                setUpTestProvider();
                connectMockLocation();
            }else{
                setUpLocationManager();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        eventBus.unregister(this);
        if(mockLocationClient != null){
            mockLocationClient.sendClose();
        }
    }

    @OnClick(R.id.stamp_button)
    void pushStampButton(){
        Intent intent = getIntent();
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        EventBusUtil.defaultBus.post(getIntent());
    }

    private void setUpTestProvider(){
        if(mLocationManager.isProviderEnabled(providerName)){
            mLocationManager.addTestProvider(providerName, true, true, true, true, true, true, true, Criteria.POWER_HIGH, Criteria.ACCURACY_HIGH);
        }

        testLocation = new Location(providerName);
        testLocation.setAccuracy(Criteria.ACCURACY_HIGH);
        testLocation.setTime(System.currentTimeMillis());
        testLocation.setElapsedRealtimeNanos(System.currentTimeMillis());
    }

    private void connectMockLocation(){
        new Thread(){
            @Override
            public void run(){
                try {
                    mockLocationClient = new WebSocketFactory().createSocket("ws://"+ Url.HOST + ":" + Url.PORT + "/stamp-rally/location", 5000)
                            .connect()
                            .addListener(new WebSocketAdapter() {
                                @Override
                                public void onTextMessage(WebSocket websocket, String message)
                                        throws Exception {
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
    public void updateLocation(LocationEvent event){
        testLocation.setLatitude(event.getLatitude());
        testLocation.setLongitude(event.getLongitude());
        mLocationManager.setTestProviderLocation(providerName, testLocation);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(
                providerName,
                1000,
                3,
                this
        );
    }

    private void setUpLocationManager(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);       //高精度
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);      //プロバイダ条件選択なし
        String provider = mLocationManager.getBestProvider(criteria, true);     //プロバイダ取得
        mLocationManager.requestLocationUpdates(provider, 1, 1, this);          //位置情報取得
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("デバッグ", "位置情報取得");
        Log.d("デバッグ", "latitude : " + location.getLatitude() + ", longitude : " + location.getLongitude());

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mLocationManager.removeUpdates(this);
        stampButton.setEnabled(true);
        debugText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
