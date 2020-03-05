package com.hc.mapdemo.BaiduLBSDemo.mapcontrol;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.hc.mapdemo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 截图及其事件响应
 */
public class SnapShotDemo extends AppCompatActivity implements BaiduMap.SnapshotReadyCallback {

    // MapView 是地图主控件
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private EditText mEditSnapShoutLeft;
    private EditText mEditSnapShoutTop;
    private EditText mEditSnapShoutRight;
    private EditText mEditSnapShoutBottom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot);
        mEditSnapShoutLeft = (EditText) findViewById(R.id.snapleft);
        mEditSnapShoutTop = (EditText) findViewById(R.id.snaptop);
        mEditSnapShoutRight = (EditText) findViewById(R.id.snapright);
        mEditSnapShoutBottom = (EditText) findViewById(R.id.snapbottom);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
    }

    /**
     * 截图 全部地图展示区域
     */
    public void snapShotAll(View v) {
        mBaiduMap.snapshot(this);
    }

    /**
     * 截图 选取区域
     */
    public void snapShotRect(View v) {
        int left = Integer.parseInt(mEditSnapShoutLeft.getText().toString().trim());
        int top = Integer.parseInt(mEditSnapShoutTop.getText().toString().trim());
        int right = Integer.parseInt(mEditSnapShoutRight.getText().toString().trim());
        int bottom = Integer.parseInt(mEditSnapShoutBottom.getText().toString().trim());
        if (left <= right && top <= bottom) {
            if (left < mMapView.getWidth() && top < mMapView.getHeight() && right < mMapView.getWidth() && bottom < mMapView.getHeight()) {
                // 矩形区域保证left <= right top <= bottom 否则截屏失败
                Rect rect = new Rect(left, top, right, bottom);
                mBaiduMap.snapshotScope(rect, this);
            } else {
                Toast.makeText(SnapShotDemo.this, "请输入正确的数据", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SnapShotDemo.this, " 矩形区域保证left <= right top <= bottom 否则截屏失败 ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 地图截屏回调接口
     *
     * @param snapshot 截屏返回的 bitmap 数据
     */
    public void onSnapshotReady(Bitmap snapshot) {
        File file = new File("/mnt/sdcard/test.png");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (snapshot.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
            Toast.makeText(SnapShotDemo.this, "屏幕截图成功，图片存在: " + file.toString(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }
}

