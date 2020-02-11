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
百度地图 SDK 地图容器有两周： GLSurfaceView 和  TextureView,都是 OpenGL ES 组件。

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





