package com.hc.mapdemo;



import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.TextureSupportMapFragment;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        LatLng GEO_BEIJING = new LatLng(39.945, 116.404);
        LatLng GEO_SHANGHAI = new LatLng(31.227, 121.481);

        //北京为地图中心，logo在左上角
        MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(GEO_BEIJING);
        SupportMapFragment map1 = (SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map1));
        map1.getBaiduMap().setMapStatus(status1);
        map1.getMapView().setLogoPosition(LogoPosition.logoPostionleftTop);

        //上海为地图中心
        MapStatusUpdate status2 = MapStatusUpdateFactory.newLatLng(GEO_SHANGHAI);
        SupportMapFragment map2 = (SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map2));
        map2.getBaiduMap().setMapStatus(status2);






    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
      //  mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        //mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
       // mMapView.onDestroy();
    }
}
