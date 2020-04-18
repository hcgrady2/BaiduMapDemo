package com.hc.mapdemo.ONSDKDemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.hc.mapdemo.ONSDKDemo.DemoRouteResultFragment;
import com.hc.mapdemo.R;

import java.util.ArrayList;
import java.util.List;

public class DemoDrivingActivity extends FragmentActivity {

    private FrameLayout mFl_retry;

    private BNRoutePlanNode mStartNode = null;
    private BNRoutePlanNode mEndNode = null;
    private ViewGroup mMapContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        mMapContent = findViewById(R.id.map_container);
        BaiduNaviManagerFactory.getMapManager().attach(mMapContent);

        mFl_retry = findViewById(R.id.fl_retry);

        initListener();
        initRoutePlanNode();
    }

    private void initRoutePlanNode() {
        mStartNode = new BNRoutePlanNode.Builder()
                .latitude(40.050969)
                .longitude(116.300821)
                .name("百度大厦")
                .description("百度大厦")
                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                .build();
        mEndNode = new BNRoutePlanNode.Builder()
                .latitude(39.908749)
                .longitude(116.397491)
                .name("北京天安门")
                .description("北京天安门")
                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                .build();
        routePlan(mStartNode, mEndNode);
    }

    private void initListener() {
        findViewById(R.id.ll_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routePlan(mStartNode, mEndNode);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        BaiduNaviManagerFactory.getMapManager().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaiduNaviManagerFactory.getMapManager().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaiduNaviManagerFactory.getMapManager().detach(mMapContent);
    }

    private void routePlan(BNRoutePlanNode sNode, BNRoutePlanNode eNode) {
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);

        BaiduNaviManagerFactory.getRoutePlanManager().routeplan(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(getApplicationContext(),
                                        "算路开始", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                mFl_retry.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),
                                        "算路成功", Toast.LENGTH_SHORT).show();

                                FragmentManager fm = getSupportFragmentManager();
                                FragmentTransaction tx = fm.beginTransaction();
                                DemoRouteResultFragment fragment = new DemoRouteResultFragment();
                                tx.add(R.id.fragment_content, fragment, "RouteResult");
                                tx.commit();
                                break;



                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                mFl_retry.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(),
                                        "算路失败", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
