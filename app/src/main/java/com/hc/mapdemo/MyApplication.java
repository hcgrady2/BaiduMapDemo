package com.hc.mapdemo;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.hc.mapdemo.Location.service.LocationService;

public class MyApplication extends Application {


    //百度地图定位使用
    public LocationService locationService;
    public Vibrator mVibrator;
    private static MyApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();


        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);



        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

      //  initEngineManager(this);

    }


//    public void initEngineManager(Context context) {
//        if (mBMapManager == null) {
//            mBMapManager = new BMapManager(context);
//        }
//
//        if (!mBMapManager.init(new MyGeneralListener())) {
//            Toast.makeText(MyApplication.getInstance().getApplicationContext(), "BMapManager  初始化错误!",
//                    Toast.LENGTH_LONG).show();
//        }
//        Log.d("ljx", "initEngineManager");
//    }

//    public static MyApplication getInstance() {
//        return mInstance;
//    }
//
//    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
//   public static class MyGeneralListener implements MKGeneralListener {
//
//        @Override
//        public void onGetPermissionState(int iError) {
//            // 非零值表示key验证未通过
//            if (iError != 0) {
//                // 授权Key错误：
//                Toast.makeText(MyApplication.getInstance().getApplicationContext(),
//                        "请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError, Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(MyApplication.getInstance().getApplicationContext(), "key认证成功", Toast.LENGTH_LONG)
//                        .show();
//            }
//        }
//    }
}
