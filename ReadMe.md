### 百度地图使用
前期准备  
1、manifest 注册key ,声明权限  
2、application 中对 sdk 进行初始化,这里可以控制坐标系。  
3、注意管理地图的生命周期

#### 添加百度地图的两种方式

1、layout 中添加 MapView

2、代码中初始化，有两种方式
```text

MapView mapView = new MapView(this);

```
第二种

```
定义BaiduMapOptions对象
BaiduMapOptions options = new BaiduMapOptions();
2设置需要的状态
//设置地图模式为卫星地图
options.mapType(BaiduMap.MAP_TYPE_SATELLITE);
3创建MapView对象
MapView mapView = new MapView(this, options);
4添加MapView对象
setContentView(mapView);

```


BaiduMapOptions 支持的状态：

状态	含义  
mapStatus	地图状态  
compassEnable	是否开启指南针，默认开启  
mapType	地图模式，默认为普通地图  
rotateGesturesEnabled	是否允许地图旋转手势，默认允许  
scrollGesturesEnabled	是否允许拖拽手势，默认允许  
overlookingGesturesEnabled	是否允许俯视图手势，默认允许  
zoomControlsEnabled	是否显示缩放按钮控件，默认允许  
zoomControlsPosition	设置缩放控件位置  
zoomGesturesEnabled	是否允许缩放手势，默认允许  
scaleControlEnabled	是否显示比例尺控件，默认显示  
scaleControlPosition	设置比例尺控件位置  
logoPosition	设置Logo位置


#### 调整百度地图的缩放层级
```text
MapStatus.Builder builder = new MapStatus.Builder();
builder.zoom(18.0f);
mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
```


#### 地图容器介绍
百度地图 SDK 地图容器有两种： GLSurfaceView 和  TextureView,都是 OpenGL ES 组件。

.GLSurfaceView:  
包括MapView，MapFragment和SupportMapFragment三种容器。  
MapFragment和SupportMapFragment用于在 Android Fragment 中放置地图。适合需要使用Fragment的开发场景，方便您实现灵活的布局。  
MapFragment和SupportMapFragment分别是android.app.Fragment和android.support.v4.app,Fragment的子类，  
MapFragment需要在android3.0以上的版本中使用。

.TextureView:  
包括TexureMapView，TextureMapFragment和TextureSupportMapFragment三种容器。  
使用场景：您将MapView与其他的GLSurfaceView（比如相机）叠加展示，或者是在ScrollView中加载地图时，  
建议使用TextureMapView及SupportTextureMapFragment来展示地图，可以有效解决 GLSurfaceView 叠加时出现的穿透、滚动黑屏等问题。  
如果使用TextureMapView请确保：Android 4.4以上系统，在AndroidManifest.xml中配置硬件加速选项。



#### 百度地图类型
地图SDK提供了3种预置的地图类型，包括普通地图，卫星图，空白地图。另外提供了2种常用图层实时路况图以及百度城市热力图。

其中空白地图：
无地图瓦片,地图将渲染为空白地图。不加载任何图块，将不会使用流量下载基础地图瓦片图层。支持叠加任何覆盖物。

适用场景:与瓦片图层（tileOverlay）一起使用，节省流量，提升自定义瓦片图下载速度。



##### 预置类型使用
```
mMapView = (MapView) findViewById(R.id.bmapView);  
mBaiduMap = mMapView.getMap();  
//普通地图 ,mBaiduMap是地图控制器对象
mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);   

//卫星地图   
mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);  


//空白地图 
mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);


```

##### 路况图和热力图
```

mMapView = (MapView) findViewById(R.id.bmapView); 
mBaiduMap = mMapView.getMap();  
//开启交通图   
mBaiduMap.setTrafficEnabled(true);

//自定义颜色
mBaiduMap.setTrafficEnabled(true);
mBaiduMap.setCustomTrafficColor("#ffba0101", "#fff33131", "#ffff9e19", "#00000000");
//  对地图状态做更新，否则可能不会触发渲染，造成样式定义无法立即生效。
MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(13);
mBaiduMap.animateMapStatus(u);



mMapView = (MapView) findViewById(R.id.bmapView); 
mBaiduMap = mMapView.getMap();  
//开启热力图  
mBaiduMap.setBaiduHeatMapEnabled(true);

```

#### 百度地图中的定位显示
在国内获得的坐标系类型可以是：国测局坐标、百度墨卡托坐标 和 百度经纬度坐标。  
1、需要在 manifest 中声明定位权限，和定位 Service
2、开启地图定位图层
```
mBaiduMap.setMyLocationEnabled(true);

```
3、我们通过继承抽象类BDAbstractListener并重写其onReceieveLocation方法来获取定位数据，并将其传给MapView。
```
ublic class MyLocationListener extends BDAbstractLocationListener {
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
            mBaiduMap.setMyLocationData(locData);
    }
}
```

4、通过LocationClient发起定位
也就是，locationClient 回开始定位，通过自己写的内部类回调获取定位信息，并传递给 BaidMap
```
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
mLocationClient.start();

```

5、正确管理生命周期
```
@Override
protected void onResume() {
    mMapView.onResume();
    super.onResume();
}

@Override
protected void onPause() {
    mMapView.onPause();
    super.onPause();
}

@Override
protected void onDestroy() {
    mLocationClient.stop();
    mBaiduMap.setMyLocationEnabled(false);
    mMapView.onDestroy();
    mMapView = null;
    super.onDestroy();
}
```
6、自定义定位有关内容
通过MyLocationConfiguration类来构造包括定位的属性，定位模式、是否开启方向、设置自定义定位图标、精度圈填充颜色以及精度圈边框颜色5个属性。

```
mBaiduMap.setMyLocationConfiguration(mLocationConfiguration)

```

#### 个性地图
可以改变元素的颜色，样式，是否显示等。  
需要下载 json 文件，然后读取加载 json 使样式生效。

#### 控件及手势
控件包括，地图logo，比例尺，缩放按钮  
手势包括：平移，缩放，俯视，旋转。

#### 地图相关事件监听
地图状态改变接口 ：OnMapStatusChangeListener  
地图单击接口 ： OnMapClickListener  
双击接口 ： OnMapDoubleClickListener  
长按事件监听 ： OnMapLongClickListener  
地图 Marker 覆盖物点击事件监听接口：OnMarkerClickListener  
触摸地图回调接口： OnMapTouchListener  
地图加载完成回调接口： OnMapLoadedCallback


#### 地图相关方法交互
1、
根据场景的不同可以分别通过MapStatusUpdateFactory类的zoomTo、zoomIn、zoomOut以及zoomBy四种方法改变地图缩放级别。animateMapStatus 可以使用渐变动画。

2、设置地图显示范围  
mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(bounds));

3、设置中心点  
mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(LatLng latLng));


4、隐藏地图标注  
mBaiduMap.showMapPoi(false);


#### 地图元素压盖顺序
1.自定义View（MapView.addView(View);）；

2.弹出窗图层（InfoWindow）；

3.定位图层（BaiduMap.setMyLocationEnabled(true);）；

4.指南针图层（当地图发生旋转和视角变化时，默认出现在左上角的指南针）；

5.标注图层（Marker），文字绘制图层（Text）；

6.几何图形图层（点、折线、弧线、圆、多边形）；

7.底图标注（指的是底图上面自带的那些POI元素）；

8.百度城市热力图（BaiduMap.setBaiduHeatMapEnabled(true);）；

9.实时路况图图层（BaiduMap.setTrafficEnabled(true);）；

10.热力图图层（HeatMap）；

11.地形图图层（GroundOverlay）；

12.瓦片图层（TileOverlay）；

13.基础底图（包括底图、底图道路、卫星图、室内图等）；



#### Marker 相关
通过 Marker 可以往地图上添加一些点标记图片等。MarkerOptions 可以控制 Marker 等属性。包括：

icon: 图标，动画类型，透明度，是否可以拖拽，旋转，是否可见等。

常用等 Marker 相关事件：

单击事件 OnMarkerClickListener

拖拽事件： OnMarkerDragListener



#### 绘制线

OverlayOptions 支持绘制分段绘制颜色和纹理。
```
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
Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
```


#### 弧、圆和多边形

弧由三个点确定
```
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
```

圆需要圆心，可以指定边框和颜色
```
//圆心位置
LatLng center = new LatLng(39.90923, 116.447428);

//构造CircleOptions对象
CircleOptions mCircleOptions = new CircleOptions().center(center)
        .radius(1400)
        .fillColor(0xAA0000FF) //填充颜色
        .stroke(new Stroke(5, 0xAA00ff00)); //边框宽和边框颜色

//在地图上显示圆
Overlay mCircle = mBaiduMap.addOverlay(mCircleOptions);
```

多边形至少需要三个点。


#### 文字和信息窗
文字（Text）在地图上也是一种覆盖物，由Text类定义。文字覆盖物的绘制通过TextOptions类来设置。

下面简述两种构造InfoWindow的方法。
使用View构造InfoWindow

使用BitmpDescriptor构造InfoWindow



#### maker动画
Marker 支持帧动画，旋转，缩放，平移，透明等组合动画。

####  点聚合功能
新增点聚合功能，可以通过缩小地图层级，将定义范围内的大量标注点聚合显示成一个标注点。




#### 绘制 Overlay 与热力图
自定义图片图层（图片覆盖物GroundOvelay），是一种位于底图和底图标注层之间的特殊Overlay，该图层不会遮挡地图标注信息。通过GroundOverlayOptions类来设置，开发者可以通过GroundOverlayOptions类设置一张图片，该图片可随地图的平移、缩放、旋转等操作做相应的变换。


#### 瓦片图层
适用于开发者拥有某一区域的地图，并希望使用此区域地图覆盖相应位置的百度地图。




