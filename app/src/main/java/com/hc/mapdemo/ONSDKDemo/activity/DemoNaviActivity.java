package com.hc.mapdemo.ONSDKDemo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviCommonParams;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.hc.mapdemo.R;

import java.util.ArrayList;
import java.util.List;

public class DemoNaviActivity extends Activity {

    private Button mWgsNaviBtn = null;
    private Button mGcjNaviBtn = null;
    private Button mBdmcNaviBtn = null;
    private Button mSzNaviBtn = null;
    private Button mBjNaviBtn = null;
    private Button mCustomNaviBtn = null;
    private Button mDb06ll = null;

    private double mCurrentLat;
    private double mCurrentLng;
    private LocationManager mLocationManager;

    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLat = location.getLatitude();
            mCurrentLng = location.getLongitude();
            Toast.makeText(DemoNaviActivity.this, mCurrentLat
                    + "--" + mCurrentLng, Toast.LENGTH_LONG).show();
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        mWgsNaviBtn = findViewById(R.id.wgsNaviBtn);
        mGcjNaviBtn = findViewById(R.id.gcjNaviBtn);
        mBdmcNaviBtn = findViewById(R.id.bdmcNaviBtn);
        mDb06ll = findViewById(R.id.mDb06llNaviBtn);
        mSzNaviBtn = findViewById(R.id.szNaviBtn);
        mBjNaviBtn = findViewById(R.id.bjNaviBtn);
        mCustomNaviBtn = findViewById(R.id.customNaviBtn);

        initListener();
        initLocation();
    }

    private void initLocation() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 1000, mLocationListener);
        }
    }

    private void initListener() {
        if (mWgsNaviBtn != null) {
            mWgsNaviBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        calRoutePlanNode(BNRoutePlanNode.CoordinateType.WGS84);
                    }
                }

            });
        }
        if (mGcjNaviBtn != null) {
            mGcjNaviBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        calRoutePlanNode(BNRoutePlanNode.CoordinateType.GCJ02);
                    }
                }

            });
        }
        if (mBdmcNaviBtn != null) {
            mBdmcNaviBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        calRoutePlanNode(BNRoutePlanNode.CoordinateType.BD09_MC);
                    }
                }
            });
        }

        if (mDb06ll != null) {
            mDb06ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        calRoutePlanNode(BNRoutePlanNode.CoordinateType.BD09LL);
                    }
                }
            });
        }

        if (mSzNaviBtn != null) {
                    mSzNaviBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        if (mCurrentLat == 0 && mCurrentLng == 0) {
                            return;
                        }
                        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                                .latitude(mCurrentLat)
                                .longitude(mCurrentLng)
                                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                                .build();
                        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                                .latitude(22.613435)
                                .longitude(114.025550)
                                .name("深圳北站")
                                .description("深圳北站")
                                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                                .build();

                        routePlanToNavi(sNode, eNode);
                    }
                }
            });
        }

        if (mBjNaviBtn != null) {
            mBjNaviBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        if (mCurrentLat == 0 && mCurrentLng == 0) {
                            return;
                        }
                        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                                .latitude(mCurrentLat)
                                .longitude(mCurrentLng)
                                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                                .build();
                        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                                .latitude(39.908749)
                                .longitude(116.397491)
                                .name("北京天安门")
                                .description("北京天安门")
                                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                                .build();

                        routePlanToNavi(sNode, eNode);
                    }
                }
            });
        }

        if (mCustomNaviBtn != null) {
            mCustomNaviBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        View dialogView = View.inflate(DemoNaviActivity.this, R.layout
                                .dialog_node, null);
                        final EditText editStart = dialogView.findViewById(R.id.edit_start);
                        final EditText editEnd = dialogView.findViewById(R.id.edit_end);
                        new AlertDialog.Builder(DemoNaviActivity.this)
                                .setView(dialogView)
                                .setPositiveButton("导航", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String startPoint = editStart.getText().toString().trim();
                                        String endPoint = editEnd.getText().toString().trim();
                                        if (!checkValid(startPoint, endPoint)) {
                                            Toast.makeText(DemoNaviActivity.this, "填写格式有误", Toast
                                                    .LENGTH_SHORT).show();
                                            return;
                                        }
                                        String[] starts = startPoint.split(",");
                                        String[] ends = endPoint.split(",");
                                        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                                                .latitude(Double.parseDouble(starts[1]))
                                                .longitude(Double.parseDouble(starts[0]))
                                                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                                                .build();
                                        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                                                .latitude(Double.parseDouble(ends[1]))
                                                .longitude(Double.parseDouble(ends[0]))
                                                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                                                .build();

                                        routePlanToNavi(sNode, eNode);
                                    }
                                })
                                .show();
                    }
                }
            });
        }

    }

    private void calRoutePlanNode(final int coType) {
        if (!BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
            Toast.makeText(DemoNaviActivity.this, "还未初始化!",
                    Toast.LENGTH_SHORT).show();
        }

        BNRoutePlanNode sNode = null, eNode = null;
        switch (coType) {
            case BNRoutePlanNode.CoordinateType.GCJ02: {
                sNode = new BNRoutePlanNode.Builder()
                        .latitude(40.05087)
                        .longitude(116.30142)
                        .name("百度大厦")
                        .description("百度大厦")
                        .coordinateType(coType)
                        .build();
                eNode = new BNRoutePlanNode.Builder()
                        .latitude(39.90882)
                        .longitude(116.39750)
                        .name("北京天安门")
                        .description("北京天安门")
                        .coordinateType(coType)
                        .build();
                break;
            }
            case BNRoutePlanNode.CoordinateType.WGS84: {
                sNode = new BNRoutePlanNode.Builder()
                        .latitude(40.050969)
                        .longitude(116.300821)
                        .name("百度大厦")
                        .description("百度大厦")
                        .coordinateType(coType)
                        .build();
                eNode = new BNRoutePlanNode.Builder()
                        .latitude(39.908749)
                        .longitude(116.397491)
                        .name("北京天安门")
                        .description("北京天安门")
                        .coordinateType(coType)
                        .build();
                break;
            }
            case BNRoutePlanNode.CoordinateType.BD09_MC: {
                sNode = new BNRoutePlanNode.Builder()
                        .latitude(4846474)
                        .longitude(12947471)
                        .name("百度大厦")
                        .description("百度大厦")
                        .coordinateType(coType)
                        .build();
                eNode = new BNRoutePlanNode.Builder()
                        .latitude(4825947)
                        .longitude(12958160)
                        .name("北京天安门")
                        .description("北京天安门")
                        .coordinateType(coType)
                        .build();
                break;
            }
            case BNRoutePlanNode.CoordinateType.BD09LL: {
                sNode = new BNRoutePlanNode.Builder()
                        .latitude(40.057009624099436)
                        .longitude(116.30784537597782)
                        .name("百度大厦")
                        .description("百度大厦")
                        .coordinateType(coType)
                        .build();
                eNode = new BNRoutePlanNode.Builder()
                        .latitude(39.915160800132085)
                        .longitude(116.40386525193937)
                        .name("北京天安门")
                        .description("北京天安门")
                        .coordinateType(coType)
                        .build();
                break;
            }
            default:
                break;
        }

        routePlanToNavi(sNode, eNode);
    }

    private void routePlanToNavi(BNRoutePlanNode sNode, BNRoutePlanNode eNode) {
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);

        BaiduNaviManagerFactory.getRoutePlanManager().routeplanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(DemoNaviActivity.this.getApplicationContext(),
                                        "算路开始", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(DemoNaviActivity.this.getApplicationContext(),
                                        "算路成功", Toast.LENGTH_SHORT).show();
                                // 躲避限行消息
                                Bundle infoBundle = (Bundle) msg.obj;
                                if (infoBundle != null) {
                                    String info = infoBundle.getString(
                                            BNaviCommonParams.BNRouteInfoKey.TRAFFIC_LIMIT_INFO
                                    );
                                    Log.d("OnSdkDemo", "info = " + info);
                                }
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(DemoNaviActivity.this.getApplicationContext(),
                                        "算路失败", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(DemoNaviActivity.this.getApplicationContext(),
                                        "算路成功准备进入导航", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(DemoNaviActivity.this,
                                        DemoGuideActivity.class);

                                startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    private boolean checkValid(String startPoint, String endPoint) {
        if (TextUtils.isEmpty(startPoint) || TextUtils.isEmpty(endPoint)) {
            return false;
        }

        if (!startPoint.contains(",") || !endPoint.contains(",")) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
