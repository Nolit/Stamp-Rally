package com.om1.stamp_rally.controller;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.StampRallyModel;
import com.om1.stamp_rally.model.event.FetchJsonEvent;
import java.io.IOException;
import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import database.entities.StampRallys;
import database.entities.Stamps;


public class MapsFragment extends Fragment implements LocationListener,OnMapReadyCallback ,NavigationView.OnNavigationItemSelectedListener {
    private final EventBus eventBus = EventBus.getDefault();
    private final LatLng START_POSITION = new LatLng(34.715602789654625,135.5946671962738);  //ビューカメラの初期化
    private static final int CAN_STAMP_METER= 50;
    private LayoutInflater inflater;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private StampRallys stampRally = null; //データベース
    @InjectView(R.id.cameraIcon_map)
    ImageButton cameraIcon;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;
    @InjectView(R.id.nav_view)
    NavigationView navigationView;
    BitmapDescriptor defaultMarker,nearMarker,completeMarker;
    ArrayList<Marker> markers = new ArrayList<Marker>();

    //debug用
    Marker oosakajo,harukasu,usj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);

        //データベースと通信
        StampRallyModel stampRallyModel = StampRallyModel.getInstance();
        stampRallyModel.fetchJson();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        setHasOptionsMenu(true);
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
        navigationView.setNavigationItemSelectedListener(this);
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        //状態別マーカーの設定
        defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        nearMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        completeMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);                     //現在地に戻るボタン(on)
        mMap.getUiSettings().setMapToolbarEnabled(false);    //mapToolBarの表示(off)
        mMap.getUiSettings().setZoomControlsEnabled(true);   //ズームボタン(on/デバッグ用)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(START_POSITION));

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
//        oosakajo = mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(34.684581, 135.526349)).title("大阪城")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//        harukasu = mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(34.645842, 135.513971)).title("あべのハルカス").alpha(0.4f));
//        usj = mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(34.6671654, 135.4356473)).title("USJ"));


        if(stampRally != null){
            Log.d("デバッグ","マーカーの配置");
            for(Stamps stamp:stampRally.getStampsCollection()){
                System.out.println(stamp.getStampName()+"を配置します");
//                markers.add(mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(stamp.getStamptableId().getLongitude(),stamp.getStamptableId().getLongitude()))
//                    .title(stamp.getStampName())));

                mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(stamp.getStamptableId().getLatitude(),stamp.getStamptableId().getLongitude()))
                    .title(stamp.getStampName()));

            }
            System.out.println("マーカーの配置完了");
        }
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

    //LocationManagerの設定
    private void setUpLocationManager(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);       //高精度
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);      //プロバイダ条件選択なし
        String provider = mLocationManager.getBestProvider(criteria, true);     //プロバイダ取得
        mLocationManager.requestLocationUpdates(provider, 1, 1, this);          //位置情報取得
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
            public void onMapClick(LatLng point) { cameraIcon.setVisibility(View.INVISIBLE);
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
    public void onProviderDisabled(String provider) { }

    @Override   //プロバイダが利用可能になった時に呼び出される
    public void onProviderEnabled(String provider) { Log.d("デバッグ","プロバイダが有効になりました:"+provider); }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);    // 位置情報の更新を止める
        eventBus.unregister(this);
    }

    @OnClick(R.id.cameraIcon_map)
    void pushCameraIcon() {

    }

    //ここから↓ NavigationDrawer
//    @Override
//    public void onBackPressed() {
//        View view = inflater.inflate(R.layout.info_window, null);
//
//        DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //ビューの取得
        View view = inflater.inflate(R.layout.info_window, null);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //NavigationViewの表示
    @OnClick(R.id.menuIcon_map)
    public void showMenu(View view) {
        Log.d("click", "showMenu");
        if(drawer.isDrawerOpen(GravityCompat.START)){
            Log.d("status", "open");
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        Log.d("status", "close");
        drawer.openDrawer(GravityCompat.START);
    }

    //データベース通信処理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchJsonEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ","データベースとの通信に失敗");
            return;
        }
        try {
            //Jsonをオブジェクトに変換
            stampRally = new ObjectMapper().readValue(event.getJson(), StampRallys.class);

//            for(Stamps i:stampRally.getStampsCollection()){
//                Log.d("デバッグ", i.getStampName());
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


