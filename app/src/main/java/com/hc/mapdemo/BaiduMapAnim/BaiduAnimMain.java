package com.hc.mapdemo.BaiduMapAnim;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.baidu.mapapi.animation.AlphaAnimation;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapapi.animation.AnimationSet;
import com.baidu.mapapi.animation.RotateAnimation;
import com.baidu.mapapi.animation.ScaleAnimation;
import com.baidu.mapapi.animation.SingleScaleAnimation;
import com.baidu.mapapi.animation.Transformation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.hc.mapdemo.BaiduLBSDemo.map.MarkerAnimationDemo;
import com.hc.mapdemo.R;

public class BaiduAnimMain extends AppCompatActivity {


    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMarkerC;
    private Marker mMarkerE;


    private LatLng llC;
    private LatLng llE;


    private static final String LTAG = MarkerAnimationDemo.class.getSimpleName();


    BitmapDescriptor bdC = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markc);
    BitmapDescriptor bdE = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marke);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bd_anim_main);


        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initOverlay();
                addView(mMapView);

            }
        });

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus status) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus status, int reason) {

            }

            @Override
            public void onMapStatusChange(MapStatus status) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus status) {

            }
        });


    }


    private void addView(MapView mapView) {
        int paddingBottom = 200;


        TextView  mTextView = new TextView(this);
        mTextView.setText(getText(R.string.instruction));
        mTextView.setTextSize(15.0f);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.BLACK);
        mTextView.setBackgroundColor(Color.parseColor("#AA00FF00"));

        MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder().position(llE);
       // builder.layoutMode(MapViewLayoutParams.ELayoutMode.absoluteMode);
        builder.layoutMode(MapViewLayoutParams.ELayoutMode.mapMode);
        builder.width(mapView.getWidth());
        builder.height(paddingBottom);
     //   builder.point(new Point(0, mapView.getHeight()));
        builder.align(MapViewLayoutParams.ALIGN_LEFT, MapViewLayoutParams.ALIGN_BOTTOM);

        mapView.addView(mTextView, builder.build());
        mapView.refreshDrawableState();




    }



    /**
     * 开启动画
     *
     * @param view
     */
    public void startAnimation(View view) {
        switch (view.getId()) {

            case R.id.btn_transformation:
                //平移动画
                startTransformation();
                break;



            default:
                break;

        }

    }

    /**
     * 初始化Overlay
     */
    public void initOverlay() {
        // add marker overlay
        llC = new LatLng(40.022211, 116.499274);

        llE = new LatLng(39.862009, 116.394064);

        MarkerOptions ooC = new MarkerOptions().position(llC).icon(bdC);
        mMarkerC = (Marker) (mBaiduMap.addOverlay(ooC));



        MarkerOptions ooE = new MarkerOptions().position(llE).icon(bdE);
        mMarkerE = (Marker) (mBaiduMap.addOverlay(ooE));



        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(12.0f);
        mBaiduMap.setMapStatus(msu);

    }


    /**
     * 开启平移动画
     */
    public void startTransformation() {
        mMarkerC.setAnimation(getTransformation());
        mMarkerC.startAnimation();

    }


    /**
     * 得到单独缩放动画类
     */
    public Animation getSingleScaleAnimation() {
        SingleScaleAnimation mSingleScale =
                new SingleScaleAnimation(SingleScaleAnimation.ScaleType.SCALE_X, 1f, 2f, 1f);
        mSingleScale.setDuration(1000);
        mSingleScale.setRepeatCount(1);
        mSingleScale.setRepeatMode(Animation.RepeatMode.RESTART);
        return mSingleScale;
    }





    /**
     * 创建平移动画
     */
    private Animation getTransformation() {
        Point point = mBaiduMap.getProjection().toScreenLocation(llC);
        LatLng latLng1 = mBaiduMap.getProjection().fromScreenLocation(new Point(point.x, point.y - 100));
        Transformation mTransforma = new Transformation(llC, latLng1, llC);
        mTransforma.setDuration(500);
        mTransforma.setRepeatMode(Animation.RepeatMode.RESTART);//动画重复模式
        mTransforma.setRepeatCount(1);//动画重复次数
        mTransforma.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {

            }
        });

        return mTransforma;
    }


    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMarkerC.cancelAnimation();

        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        markerRemove();
        super.onDestroy();
        // 回收 bitmap 资源
        bdC.recycle();

        bdE.recycle();


    }

    public void markerRemove() {
        mMarkerC.remove();
    }

}
