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
        EasyPermissions.RationaleCallbacks{


    //动态权限申请
    private static final String[] LOCATION_AND_CAMERA=
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_LOCATION_CAMERA_PERM = 124;




    private static final String TAG = "MapStatus";
    private MapView mMapView = null;
    private  BaiduMap mBaiduMap;
    private LocationClient mLocationClient;



    // 声明json文件变量
    private static final String CUSTOM_FILE_NAME_GRAY = "custom_map_config_white.json";


    Button button;



    //opengl

    // 3D立方体顶点绘制顺序列表
    private short[] mDrawIndices = {
            0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7, 3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6, 5, 3, 0, 1, 3, 1, 2
    };

    // 3D立方体8个顶点颜色值
    private float[] mVertexColors = {
            1f, 1f, 0f, 1f,
            0f, 1f, 1f, 1f,
            1f, 0f, 1f, 1f,
            0f, 0f, 0f, 1f,
            1f, 1f, 1f, 1f,
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f
    };

    // 立方体顶点坐标Buffer
    private FloatBuffer mVertextBuffer;
    // 顶点绘制顺序Buffer
    private ShortBuffer mIndexBuffer;
    // 立方体顶点颜色Buffer
    private FloatBuffer mColorBuffer;
    // 3D立方体着色器
    private CubeShader mCubeShader;





    private void initCubeModelData(float width, float height, float depth) {
        // 对标墨卡托坐标
        width = width * 10000 / 2;
        height = height * 10000 / 2;
        depth = depth * 10000 / 2;

        // 立方体8个顶点坐标
        float[] vertices = {
                -width, -height, -0,
                width, -height, -0,
                width, height, -0,
                -width, height, -0,
                -width, -height, depth,
                width, -height, depth,
                width, height, depth,
                -width, height, depth,
        };

        mVertextBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertextBuffer.put(vertices).position(0);

        // 立方体顶点绘制顺序Buffer
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(mDrawIndices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mIndexBuffer = byteBuffer.asShortBuffer();
        mIndexBuffer.put(mDrawIndices);
        mIndexBuffer.position(0);

        // 立方体顶点颜色Buffer
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(mVertexColors.length * 4);
        byteBuffer1.order(ByteOrder.nativeOrder());
        mColorBuffer = byteBuffer1.asFloatBuffer();
        mColorBuffer.put(mVertexColors);
        mColorBuffer.position(0);
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       // locationAndCameraTask();

        button = findViewById(R.id.button);




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



        // 获取json文件路径
        String customStyleFilePath = getCustomStyleFilePath(MainActivity.this, CUSTOM_FILE_NAME_GRAY);
        // 设置个性化地图样式文件的路径和加载方式
        mMapView.setMapCustomStylePath(customStyleFilePath);
        // 动态设置个性化地图样式是否生效
        mMapView.setMapCustomStyleEnable(true);


        mMapView.setLogoPosition(LogoPosition.logoPostionRightTop);

        //实例化UiSettings类对象
         UiSettings mUiSettings = mBaiduMap.getUiSettings();
        //通过设置enable为true或false 选择是否显示指南针
        //mUiSettings.setCompassEnabled(enabled);
        //通过设置enable为true或false 选择是否启用地图平移
       // mUiSettings.setScrollGesturesEnabled(false);
        //mUiSettings.setZoomGesturesEnabled(false);
        //mUiSettings.setOverlookingGesturesEnabled(false);
        ///mUiSettings.setRotateGesturesEnabled(false);



        BaiduMap.OnMapStatusChangeListener listener = new BaiduMap.OnMapStatusChangeListener() {
                /**
                 * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
                 *
                 * @param status 地图状态改变开始时的地图状态
                 */
                @Override
                public void onMapStatusChangeStart(MapStatus status) {
                    Log.i(TAG, "onMapStatusChangeStart: ");

                }
                /**
                 * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
                 *
                 * @param status 地图状态改变开始时的地图状态
                 *
                 * @param reason 地图状态改变的原因
                 */

                //用户手势触发导致的地图状态改变,比如双击、拖拽、滑动底图
                //int REASON_GESTURE = 1;
                //SDK导致的地图状态改变, 比如点击缩放控件、指南针图标
                //int REASON_API_ANIMATION = 2;
                //开发者调用,导致的地图状态改变
                //int REASON_DEVELOPER_ANIMATION = 3;
                @Override
                public void onMapStatusChangeStart(MapStatus status, int reason) {
                    Log.i(TAG, "onMapStatusChangeStart: reason:" + reason);
                }

                /**
                 * 地图状态变化中
                 *
                 * @param status 当前地图状态
                 */
                @Override
                public void onMapStatusChange(MapStatus status) {

                    Log.i(TAG, "onMapStatusChange: ");
                }

                /**
                 * 地图状态改变结束
                 *
                 * @param status 地图状态改变结束后的地图状态
                 */
                @Override
                public void onMapStatusChangeFinish(MapStatus status) {

                    Log.i(TAG, "onMapStatusChangeFinish: ");

                }
            };


            //设置地图状态监听
         //   mBaiduMap.setOnMapStatusChangeListener(listener);


        BaiduMap.OnMapClickListener clickListener = new BaiduMap.OnMapClickListener() {
            /**
             * 地图单击事件回调函数
             *
             * @param point 点击的地理坐标
             */
            @Override
            public void onMapClick(LatLng point) {

                Log.i(TAG, "onMapClick: 获取地理坐标："+ point.latitude);
            }

            /**
             * 地图内 Poi 单击事件回调函数
             *
             * @param mapPoi 点击的 poi 信息
             */
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

                Log.i(TAG, "onMapPoiClick: 单击回调：" + mapPoi.getName());
            }
        };
//设置地图单击事件监听
    //    mBaiduMap.setOnMapClickListener(clickListener);




        BaiduMap.OnMapDoubleClickListener doubleClickListener = new BaiduMap.OnMapDoubleClickListener() {
            /**
             * 地图双击事件监听回调函数
             *
             * @param point 双击的地理坐标
             */
            @Override
            public void onMapDoubleClick(LatLng point) {

                Log.i(TAG, "onMapDoubleClick: 双击回调" + point.latitude);
            }
        };

//设置地图双击事件监听
      //  mBaiduMap.setOnMapDoubleClickListener(doubleClickListener);




        BaiduMap.OnMapLongClickListener longClickListener = new BaiduMap.OnMapLongClickListener() {
            /**
             * 地图长按事件监听回调函数
             *
             * @param point 长按的地理坐标
             */
            @Override
            public void onMapLongClick(LatLng point) {

                Log.i(TAG, "onMapLongClick: 长按回调");

                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));

            }
        };
//设置地图长按事件监听
//
mBaiduMap.setOnMapLongClickListener(longClickListener);




        BaiduMap.SnapshotReadyCallback callback = new BaiduMap.SnapshotReadyCallback() {

            /**
             * 地图截屏回调接口
             *
             * @param snapshot 截屏返回的 bitmap 数据
             */
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                Log.i(TAG, "onSnapshotReady: 地图截屏回调");

            }
        };

/**
 * 发起截图请求
 *
 * @param callback 截图完成后的回调
 */
        mBaiduMap.snapshot(callback);


        mBaiduMap.setViewPadding(202, 20 , 20, 20);



        //隐藏地图标注，只显示道路信息
//默认显示地图标注
        mBaiduMap.showMapPoi(false);








        //添加 Marker
        //定义Maker坐标点
//定义Maker坐标点
        LatLng point = new LatLng(39.944251, 116.494996);
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_mark);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point) //必传参数
                .icon(bitmap) //必传参数
                .draggable(true)
                .animateType(MarkerOptions.MarkerAnimateType.jump)
//设置平贴地图，在地图中双指下拉查看效果
                .flat(true)
                .alpha(0.5f);
//在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);



        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            //marker被点击时回调的方法
            //若响应点击事件，返回true，否则返回false
            //默认返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(TAG, "onMarkerClick: 点击了 marker");
                return true;
            }
        });





        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {

            //在Marker拖拽过程中回调此方法，这个Marker的位置可以通过getPosition()方法获取
            //marker 被拖动的Marker对象
            @Override
            public void onMarkerDrag(Marker marker) {
                //对marker处理拖拽逻辑
                Log.i(TAG, "onMarkerDrag: ");
            }

            //在Marker拖动完成后回调此方法， 这个Marker的位可以通过getPosition()方法获取
            //marker 被拖拽的Marker对象
            @Override
            public void onMarkerDragEnd(Marker marker) {

                Log.i(TAG, "onMarkerDragEnd: ");
            }

            //在Marker开始被拖拽时回调此方法， 这个Marker的位可以通过getPosition()方法获取
            //marker 被拖拽的Marker对象
            @Override
            public void onMarkerDragStart(Marker marker) {

                Log.i(TAG, "onMarkerDragStart: ");
            }
        });





        //绘制线段
        //构建折线点坐标
//构建折线点坐标
        List<LatLng> points = new ArrayList<LatLng>();
        points.add(new LatLng(39.965,116.404));
        points.add(new LatLng(39.925,116.454));
        points.add(new LatLng(39.955,116.494));
        points.add(new LatLng(39.905,116.554));
        points.add(new LatLng(39.965,116.604));

        List<Integer> colors = new ArrayList<>();
        colors.add(Integer.valueOf(Color.BLUE));
        colors.add(Integer.valueOf(Color.RED));
        colors.add(Integer.valueOf(Color.YELLOW));
        colors.add(Integer.valueOf(Color.GREEN));

//设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(0xAAFF0000)
                .points(points)
                .colorsValues(colors);//设置每段折线的颜色

//在地图上绘制折线
//mPloyline 折线对象
    //    Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);





     //绘制弧
        // 添加弧线坐标数据
        LatLng p1 = new LatLng(39.97923, 116.357428);//起点
        LatLng p2 = new LatLng(39.94923, 116.397428);//中间点
        LatLng p3 = new LatLng(39.97923, 116.437428);//终点

//构造ArcOptions对象
        OverlayOptions mArcOptions = new ArcOptions()
                .color(Color.RED)
                .width(10)
                .points(p1, p2, p3);

//在地图上显示弧线
        Overlay mArc = mBaiduMap.addOverlay(mArcOptions);




        //绘制圆
        //圆心位置
        LatLng center = new LatLng(39.90923, 116.447428);

//构造CircleOptions对象
        CircleOptions mCircleOptions = new CircleOptions().center(center)
                .radius(1400)
                .fillColor(0xAA0000FF) //填充颜色
                .stroke(new Stroke(5, 0xAA00ff00)); //边框宽和边框颜色

//在地图上显示圆
     //   Overlay mCircle = mBaiduMap.addOverlay(mCircleOptions);


        //绘制多边形
        //多边形顶点位置
        List<LatLng> pointsss = new ArrayList<>();
        pointsss.add(new LatLng(39.93923, 116.357428));
        pointsss.add(new LatLng(39.91923, 116.327428));
        pointsss.add(new LatLng(39.89923, 116.347428));
        pointsss.add(new LatLng(39.89923, 116.367428));
        pointsss.add(new LatLng(39.91923, 116.387428));

//构造PolygonOptions
        PolygonOptions mPolygonOptions = new PolygonOptions()
                .points(pointsss)
                .fillColor(0xAAFFFF00) //填充颜色
                .stroke(new Stroke(5, 0xAA00FF00)); //边框宽度和颜色

//在地图上显示多边形
        mBaiduMap.addOverlay(mPolygonOptions);


        //文字覆盖物位置坐标
        LatLng llText = new LatLng(39.86923, 116.397428);

//构建TextOptions对象
        OverlayOptions mTextOptions = new TextOptions()
                .text("百度地图SDK") //文字内容
                .bgColor(0xAAFFFF00) //背景色
                .fontSize(24) //字号
                .fontColor(0xFFFF00FF) //文字颜色
                .rotate(-30) //旋转角度
                .position(llText);

//在地图上显示文字覆盖物
        Overlay mText = mBaiduMap.addOverlay(mTextOptions);





        //用来构造InfoWindow的Button
        Button button = new Button(getApplicationContext());
        button.setBackgroundResource(R.drawable.icon_mark);
        button.setText("InfoWindow");

//构造InfoWindow
//point 描述的位置点
//-100 InfoWindow相对于point在y轴的偏移量
      InfoWindow  mInfoWindow = new InfoWindow(button, point, -100);

//使InfoWindow生效
        mBaiduMap.showInfoWindow(mInfoWindow);





        //定义Ground的显示地理范围
        LatLng southwest = new LatLng(39.92235, 116.380338);
        LatLng northeast = new LatLng(39.947246, 116.414977);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(northeast)
                .include(southwest)
                .build();

//定义Ground显示的图片
        BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.drawable.icon_subway_station);
//定义GroundOverlayOptions对象
        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds)
                .image(bdGround)
                .transparency(0.8f); //覆盖物透明度

//在地图中添加Ground覆盖物
        mBaiduMap.addOverlay(ooGround);






        //opengl 使用

        BaiduMap.OnMapDrawFrameCallback mapDrawFrameCallback = new BaiduMap.OnMapDrawFrameCallback() {
            //废弃
            @Override
            public void onMapDrawFrame(GL10 gl, MapStatus drawingMapStatus) {

            }
            @Override
            public void onMapDrawFrame(MapStatus drawingMapStatus) {
                if (null == mBaiduMap.getProjection()) {
                    return;
                }
               // drawCube(drawingMapStatus);
            }
        };

        mBaiduMap.setOnMapDrawFrameCallback(mapDrawFrameCallback);






        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //直接缩放至缩放级别16
              //  mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(16));


            }
        });


    }







    /**
     * 读取json路径
     */
    private String getCustomStyleFilePath(Context context, String customStyleFileName) {
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String parentPath = null;
        try {
            inputStream = context.getAssets().open("customConfigdir/" + customStyleFileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            parentPath = context.getFilesDir().getAbsolutePath();
            File customStyleFile = new File(parentPath + "/" + customStyleFileName);
            if (customStyleFile.exists()) {
                customStyleFile.delete();
            }
            customStyleFile.createNewFile();

            outputStream = new FileOutputStream(customStyleFile);
            outputStream.write(buffer);
        } catch (IOException e) {
            Log.e("CustomMapDemo", "Copy custom style file failed", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e("CustomMapDemo", "Close stream failed", e);
                return null;
            }
        }
        return parentPath + "/" + customStyleFileName;
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

