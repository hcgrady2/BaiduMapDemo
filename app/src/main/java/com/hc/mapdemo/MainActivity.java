package com.hc.mapdemo;



import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.TextureSupportMapFragment;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.hc.mapdemo.BaiduLBSDemo.BMapApiDemoMain;
import com.hc.mapdemo.Location.LocationMain;
import com.hc.mapdemo.ONSDKDemo.ONSDKDemoMain;
import com.hc.mapdemo.my_gaode_demo.MyGaodeMain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks, View.OnClickListener {


    //动态权限申请
    private static final String[] LOCATION_AND_CAMERA=
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_LOCATION_CAMERA_PERM = 124;


    private static final String TAG = "MapStatus";

    Button btn_baidu_lbs,btn_baidu_nav,btn_location_demo,btn_mygaode_demo;
    Button btn_baidu_pano;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_baidu_lbs = findViewById(R.id.btn_lbs_demo);
        btn_baidu_lbs.setOnClickListener(this);




        btn_baidu_nav = findViewById(R.id.btn_nav_demo);
        btn_baidu_nav.setOnClickListener(this);



        btn_location_demo = findViewById(R.id.btn_location_demo);
        btn_location_demo.setOnClickListener(this);

        btn_mygaode_demo = findViewById(R.id.btn_mygaode_demo);
        btn_mygaode_demo.setOnClickListener(this);

        btn_baidu_pano = findViewById(R.id.btn_baidu_pano);
        btn_baidu_pano.setOnClickListener(this);

    }






    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Log.i(TAG, "onPermissionsGranted: 获取定位权限");



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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_lbs_demo:

                startActivity(new Intent(
                        MainActivity.this, BMapApiDemoMain.class
                ));

                break;


            case R.id.btn_nav_demo:

                startActivity(new Intent(
                        MainActivity.this, ONSDKDemoMain.class
                ));
                break;



            case R.id.btn_location_demo:
                startActivity(new Intent(MainActivity.this, LocationMain.class));
                break;



            case R.id.btn_mygaode_demo:
                startActivity(new Intent(MainActivity.this, MyGaodeMain.class));
                break;



            case R.id.btn_baidu_pano:
               // startActivity(new Intent(MainActivity.this, PanoDemoActivity.class));
                break;

            default:
                    break;
        }
    }
}

