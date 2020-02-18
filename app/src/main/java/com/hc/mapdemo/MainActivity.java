package com.hc.mapdemo;



import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.TextureSupportMapFragment;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks{


    //动态权限申请
    private static final String[] LOCATION_AND_CAMERA=
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_LOCATION_CAMERA_PERM = 124;




    private static final String TAG = "BaiduMapDemo";
    private MapView mMapView = null;
    private  BaiduMap mBaiduMap;
    private LocationClient mLocationClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       // locationAndCameraTask();
        
        


        /** 最基本的 layout 使用方式，直接在 layout 中使用 MapView 布局
         */
        //获取地图控件引用
        //mMapView = (MapView) findViewById(R.id.bmapView);
       // MapStatus.Builder builder = new MapStatus.Builder();
       // builder.zoom(21.0f);
        //mMapView.getMap().setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));




        /**
         * 使用百度自己的 Fragment 进行布局，当然不兼容 androidx
         */
//        FragmentManager manager = getSupportFragmentManager();
//        //获取到SupportMapFragment实例
//        BaiduMapOptions options = new BaiduMapOptions();
//       SupportMapFragment  mSupportMapFragment = SupportMapFragment.newInstance(options);
//        //将fragment添加至A ctivity
//        manager.beginTransaction().add(R.id.map_container, mSupportMapFragment).commit();
//       // manager.beginTransaction().replace(R.id.map_container, mSupportMapFragment).commit();
//




        /**
         * 多实例显示地图
         */
//        LatLng GEO_BEIJING = new LatLng(39.945, 116.404);
//        LatLng GEO_SHANGHAI = new LatLng(31.227, 121.481);
//
//        //北京为地图中心，logo在左上角
//        MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(GEO_BEIJING);
//        SupportMapFragment map1 = (SupportMapFragment) (getSupportFragmentManager()
//                .findFragmentById(R.id.map1));
//        map1.getBaiduMap().setMapStatus(status1);
//        map1.getMapView().setLogoPosition(LogoPosition.logoPostionleftTop);
//
//        //上海为地图中心
//        MapStatusUpdate status2 = MapStatusUpdateFactory.newLatLng(GEO_SHANGHAI);
//        SupportMapFragment map2 = (SupportMapFragment) (getSupportFragmentManager()
//                .findFragmentById(R.id.map2));
//        map2.getBaiduMap().setMapStatus(status2);






        mMapView = (MapView) findViewById(R.id.bmapView);
         mBaiduMap = mMapView.getMap();
        //普通地图 ,mBaiduMap是地图控制器对象
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);


//        mBaiduMap.setTrafficEnabled(true);
//        mBaiduMap.setCustomTrafficColor("#ffba0101", "#ffba0101", "#ffba0101", "#ffba0101");
////  对地图状态做更新，否则可能不会触发渲染，造成样式定义无法立即生效。
//        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(13);
//        mBaiduMap.animateMapStatus(u);



        //开启热力图
      //  mBaiduMap.setBaiduHeatMapEnabled(true);

        mBaiduMap.setMyLocationEnabled(true);


        Log.i(TAG, "onCreate: 准备开始定位");
        //定位初始化
        mLocationClient = new LocationClient(this);

//通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

//设置locationClientOption
        mLocationClient.setLocOption(option);

//注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
//开启地图定位图层
    //    mLocationClient.start();



        //自定义定位属性
        MyLocationConfiguration.LocationMode mode = MyLocationConfiguration.LocationMode.FOLLOWING;
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background);
        int accuracyCircleFillColor = 0xAAFFFF88;
        int accuracyCircleStrokeColor = 0xAA00FF00;
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mode,true,mCurrentMarker,accuracyCircleFillColor,accuracyCircleStrokeColor
        ));




        mBaiduMap.setIndoorEnable(true);//打开室内图，默认为关闭状态





    }


    @AfterPermissionGranted(RC_LOCATION_CAMERA_PERM)
    private void locationAndCameraTask() {

        if (hasLocationAndCamerasPermissions()) {
            // Have permissions, do the thing!
            Toast.makeText(this, "TODO: Location and Contacts things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                    "我们需要定位权限",
                    RC_LOCATION_CAMERA_PERM,
                    LOCATION_AND_CAMERA);
        }
        
        
    }

    private boolean hasLocationAndCamerasPermissions() {
        return EasyPermissions.hasPermissions(this, LOCATION_AND_CAMERA);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
       // mMapView.onDestroy();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Log.i(TAG, "onPermissionsGranted: 获取定位权限");


        //定位初始化
        mLocationClient = new LocationClient(this);

//通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

//设置locationClientOption
        mLocationClient.setLocOption(option);

//注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
//开启地图定位图层
//        mLocationClient.start();





    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }

    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }


            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            Log.i(TAG, "onReceiveLocation: 收到了定位信息:" + location.getAddrStr());


            mBaiduMap.setMyLocationData(locData);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



}

