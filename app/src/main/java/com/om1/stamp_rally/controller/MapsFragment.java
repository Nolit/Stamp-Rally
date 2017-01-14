package com.om1.stamp_rally.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.om1.stamp_rally.R;
import com.om1.stamp_rally.model.MapModel;
import com.om1.stamp_rally.model.bean.StampBean;
import com.om1.stamp_rally.model.bean.StampListAdapter;
import com.om1.stamp_rally.model.event.FetchStampRallyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import database.entities.StampRallys;
import database.entities.Stamps;

import static android.content.Context.MODE_PRIVATE;

public class MapsFragment extends Fragment implements LocationListener,OnMapReadyCallback ,NavigationView.OnNavigationItemSelectedListener {
    private final EventBus eventBus = EventBus.getDefault();
    SharedPreferences mainPref;
    private final LatLng START_POSITION = new LatLng(34.715602789654625,135.5946671962738);  //ビューカメラの初期化
    private static final int CAN_STAMP_METER= 100;
    private StampRallys stampRally; //データベース
    private LayoutInflater inflater;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LatLng position = null;        //確認！！！→　cameraIconの表示処理(VISIBLE)の時に使う。onLocationChangedが呼ばれない限り初期化されてない、onLocationChangedが更新されるまで前の位置情報が入ったままになる
    //Marker
    BitmapDescriptor defaultMarker,nearMarker,completeMarker;
    public ArrayList<Marker> markers = new ArrayList<Marker>();
    @InjectView(R.id.cameraIcon_map)
    ImageButton cameraIcon;
    //DrawerLayout・ListView
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;
    @InjectView(R.id.listView)
    ListView listView;
    StampBean stampBean;
    ArrayList<StampBean> stampList = new ArrayList<StampBean>();
    StampListAdapter adapter;
    //intent
    String stampTitle = null;
    String selectedStampId = null;
    String tryingStampRallyId = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);

        //データベース通信
        mainPref = getContext().getSharedPreferences("main", MODE_PRIVATE);
        MapModel.getInstance().fetchJson(mainPref.getString("playingStampRally", null));
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

        stampBean = new StampBean();
        stampList = new ArrayList<StampBean>();
        adapter = new StampListAdapter(getActivity());
        adapter.setStampList(stampList);
        listView.setAdapter(adapter);

        //ListViewのイベントハンドラ
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick (AdapterView < ? > parent, View view,int pos, long id) {
                ListView listView = (ListView) parent;
                StampBean stampListItem = (StampBean) listView.getItemAtPosition(pos);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(stampListItem.getStampLatLng())); //選択したスタンプタイトルのマーカーに移動
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        cameraIcon.setVisibility(View.INVISIBLE);
//        navigationView.setNavigationItemSelectedListener(this);
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
                for(StampBean bean:stampList){
                    if(marker.getId().equals(bean.getMarkerId())){
                        img.setImageBitmap(bean.getPictureBitmap());
                        break;
                    }
                }
                return view;
            }
        });

        setUpLocationManager();
        setMapListeners();
    }

    @Override   //位置座標を取得したら引数に渡して呼び出される
    public void onLocationChanged(Location location) {
        //カメラを現在地に移動
        position = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        //現在地からの距離100m以内のマーカーを切り替える（メートルで計算・WGS84楕円体）
        float[] results_markerInitialization = new float[1];
        for(Marker marker:markers) {
            Location.distanceBetween(location.getLatitude(), location.getLongitude()
                    , marker.getPosition().latitude
                    , marker.getPosition().longitude
                    , results_markerInitialization);
            Log.d("デバッグ", "現在地から"+ marker.getTitle() +"までの距離" + results_markerInitialization[0] + "m");

            if(results_markerInitialization[0] < CAN_STAMP_METER){
                marker.setIcon(nearMarker);
                Log.d("デバッグ",marker.getTitle()+"のマーカーをnearにセット");
            }else{
                marker.setIcon(defaultMarker);
                Log.d("デバッグ",marker.getTitle()+"のマーカーをdefautにセット");
            }

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
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                cameraIcon.setVisibility(View.INVISIBLE);
                float[] results_cameraIconVisible = new float[1];

                if(position != null){
                    Location.distanceBetween(position.latitude, position.longitude
                            , marker.getPosition().latitude
                            , marker.getPosition().longitude
                            , results_cameraIconVisible);
                    if(results_cameraIconVisible[0] < CAN_STAMP_METER){
                        cameraIcon.setVisibility(View.VISIBLE);

                        //intent
                        stampTitle = marker.getTitle();
                        selectedStampId = marker.getId().replaceAll("m", "");
                        selectedStampId = Integer.toString(stampList.get(Integer.valueOf(selectedStampId)).getStampId());
                        tryingStampRallyId = Integer.toString(stampRally.getStamprallyId());
                    }
                }
                return false;
            }
        });

        //地図上をタップ時のイベントハンドラ（カメラボタンを非常時にする）
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                cameraIcon.setVisibility(View.INVISIBLE);
                //intent
                selectedStampId = null;
                stampTitle = null;
            }
        });

        //infoWindowをタップ時のイベントハンドラ
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsFragment.this.getContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override   //プロバイダが利用可能になった時に呼び出される
    public void onProviderEnabled(String provider) { Log.d("デバッグ","プロバイダが有効になりました:"+provider); }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);    // 位置情報の更新を止める
        eventBus.unregister(this);
    }

    //カメラアイコン押下・カメラページへインテント
    @OnClick(R.id.cameraIcon_map)
    void pushCameraIcon(){
        Intent intent = new Intent(getContext(), TakeStampActivity.class);
        intent.putExtra("stampRegisterFlag", false);
        intent.putExtra("stampId", selectedStampId);
        intent.putExtra("stampRallyId", tryingStampRallyId);
        startActivity(intent);
    }

    //Drawer・ListView
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

    //メニューアイコン押下・DrawerLayoutの表示
    @OnClick(R.id.menuIcon_map)
    public void showMenu(View view) {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            Log.d("status", "open");
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        drawer.openDrawer(GravityCompat.START);
    }

    //データベース通信処理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fetchedJson(FetchStampRallyEvent event) {
        if (!event.isSuccess()) {
            Log.d("デバッグ:MapsFragment","データベースとの通信に失敗");
            return;
        }
        try {
            //Jsonをオブジェクトに変換
            stampRally = new ObjectMapper().readValue(event.getJson(), StampRallys.class);
            Log.d("デバッグ:MapsFragment","データベースとの通信に成功");
            stampList.clear();

            //マーカーの配置・格納
            if(stampRally != null){
                int counter = 0;
                for(Stamps stamp:stampRally.getStampList()){
                    markers.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(stamp.getStampPads().getLatitude(),stamp.getStampPads().getLongitude()))
                            .title(stamp.getStampName())));
                    stampBean = new StampBean();

                    stampBean.setMarkerId("m"+Integer.toString(counter));
                    counter++;

                    stampBean.setStampTitle(stamp.getStampName());
                    stampBean.setStampId(stamp.getStampId());
                    stampBean.setLatLng(new LatLng(stamp.getStampPads().getLatitude(),stamp.getStampPads().getLongitude()));
                    stampBean.setPictPath(stamp.getPicture());
                    stampBean.setStampComment(stamp.getStampComment());
                    stampList.add(stampBean);
                }
                adapter.notifyDataSetChanged(); //リストの更新
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}