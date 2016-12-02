package com.om1.stamp_rally.controller;


import android.app.AlertDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.om1.stamp_rally.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MapsFragment extends Fragment implements LocationListener,OnMapReadyCallback {
    private static final int CAN_STAMP_METER= 50;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    @InjectView(R.id.cameraIcon_map)
    ImageButton cameraIcon;

    //状態別マーカーの宣言
    BitmapDescriptor defaultMarker,nearMarker,completeMarker;

    //debug用
    Marker oosakajo,harukasu,usj;

    private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;

        return inflater.inflate(R.layout.activity_maps, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cameraIcon.setVisibility(View.INVISIBLE);

        //状態別マーカーの設定
        defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        nearMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        completeMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);

        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);    //現在地に戻るボタン(on)
        mMap.getUiSettings().setMapToolbarEnabled(false);   //mapToolBarの表示(off)
        mMap.getUiSettings().setZoomControlsEnabled(true);  //ズームボタン(on/デバッグ用)

        //ビューカメラの初期位置（ホノルル）→GPSが取得できない場合に表示される
        LatLng start_position = new LatLng(21.308889,-157.826111);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start_position));

        //infoWindowのカスタマイズ
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = inflater.inflate(R.layout.info_window, null);
                // タイトル設定
                TextView title = (TextView) view.findViewById(R.id.info_title);
                title.setText(marker.getTitle());
                // 画像設定
                ImageView img = (ImageView) view.findViewById(R.id.info_image);
                img.setImageResource(R.mipmap.oosakajo);
                return view;
            }
        });

        setUpLocationManager();

        setMapListeners();

        //スタンプラリーのスポット3点（デバッグ用）
        oosakajo = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(34.684581, 135.526349)).title("大阪城")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        harukasu = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(34.645842, 135.513971)).title("あべのハルカス").alpha(0.4f));
        usj = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(34.6671654, 135.4356473)).title("USJ"));

    }

    @Override   //位置座標を取得したら引数に渡して呼び出される
    public void onLocationChanged(Location location) {
        //カメラを現在地に移動
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        //現在地から大阪城までの距離を計算（メートルで計算・WGS84楕円体）
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), 34.684581, 135.526349, results);

        //マーカーとの距離が50m以内の時にマーカーを切り替える
        if(results[0] < CAN_STAMP_METER){
            oosakajo.setIcon(nearMarker);
        }else{
            oosakajo.setIcon(defaultMarker);
        }
    }

    private void setUpLocationManager(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);       //高精度
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        String provider = mLocationManager.getBestProvider(criteria, true);
        if(mLocationManager.isProviderEnabled(provider)){
            System.out.println("取得したプロバイダ:"+provider);
        }
        //位置情報取得
        mLocationManager.requestLocationUpdates(provider, 1, 1, this);
    }

    private void setMapListeners(){
        //マーカータップ時のイベントハンドラ（カメラボタンを表示する）
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                cameraIcon.setVisibility(View.VISIBLE);
                return false;
            }
        });

        //地図上をタップ時のイベントハンドラ（カメラボタンを非常時にする）
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                cameraIcon.setVisibility(View.INVISIBLE);
            }
        });

        //infoWindowをタップ時のイベントハンドラ
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsFragment.this.getContext(), "ここでスタンプ詳細表示", Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override   //プロバイダが利用可能になった時に呼び出される
    public void onProviderEnabled(String provider) {
        System.out.println("プロバイダが有効になりました:"+provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);    // 位置情報の更新を止める
    }

    @OnClick(R.id.cameraIcon_map)
    void pushCameraIcon() {

    }

}