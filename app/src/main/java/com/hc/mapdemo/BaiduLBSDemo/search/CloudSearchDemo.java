package com.hc.mapdemo.BaiduLBSDemo.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hc.mapdemo.R;


public class CloudSearchDemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_search_demo);
    }

    public void startCloudSearchDemo(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CloudSearchActivity.class);
        startActivity(intent);
    }
}
