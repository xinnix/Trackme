package com.cloudbean.trackme.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.mapcore.m;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkRouteResult;
import com.cloudbean.model.Track;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackerUtil.ByteHexUtil;
import com.cloudbean.trackerUtil.GpsCorrect;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.R.drawable;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;
import com.cloudbean.trackme.R.menu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class TraceActivity extends BaseActivity implements OnGeocodeSearchListener,
LocationSource,AMapLocationListener,OnRouteSearchListener {
	
	private MapView mapView = null;
    private AMap aMap = null;
    private Marker mMoveMarker = null;
	private ProgressDialog pd = null;
	private ToggleButton tbSatelite = null;
	private String addressName = "等待地址解析";
	private GeocodeSearch geocoderSearch = null;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	
	private RouteSearch routeSearch;
	private WalkRouteResult walkRouteResult;
	private DriveRouteResult driveRouteResult;// 驾车模式查询结果
	
	
	private final int ADDRESS_COMPLETE = 0x9901;
	private LatLonPoint endpoint;
	private Marker startMk, targetMk;
	private Button btMyPos;
	private Button btDevPos;
	private Button btTrace;
	private Button btNavi;
	
	int trackFlag =1;
	
	double lat;
	double lon;
	String speed;
	String distant;
	String date;
	String temperature;
	String gsmStrength;
	String accState;
	String voltage;
	
	
	PolylineOptions polylineOptions = null;
	Polyline poly = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapView.onCreate(savedInstanceState);// 必须要写
//	    geocoderSearch = new GeocodeSearch(this);
//		geocoderSearch.setOnGeocodeSearchListener(this);
		
	    
	}

	protected void onResume() {
		super.onResume();
		mapView.onResume();
		initPosition();
//		if(TrackApp.currentCar.lastState==null){
//			MsgEventHandler.c_sGetCarPosition(TrackApp.currentCar);
//			showMessage("定位请求已发送");
//		}
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	
	
//    private  Handler handler = new Handler() {  
//        @Override  
//        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
//         if(msg.what==CNetworkAdapter.MSG_POSITION){
//        	 Bundle b = msg.getData();
//        	 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			 String devid = b.getString("devid");
//			 if(devid.equals(TrackApp.currentCar.devId)){
//				 	TrackApp.currentCar.setAlive(true);
//					 lat = b.getDouble("lat");
//		        	 lon = b.getDouble("lon");
//		        	 speed = b.getString("speed");
//		        	 distant = b.getString("ditant");
//					 date = format.format(new Date());
//					 voltage = b.getString("voltage");
//					 gsmStrength = b.getString("gsmStrength");
//					 double[] correctCoordinate = new double[2];
//					 GpsCorrect.transform(lat, lon, correctCoordinate);
//					 
//					 
////					 LatLonPoint latLonPoint =new LatLonPoint(correctCoordinate[0], correctCoordinate[1]);
////					 RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
////					 geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
//					 
//					
//						
//					
//				 	MarkerOptions markerOptions = new MarkerOptions();
//					markerOptions.anchor(0.5f, 0.5f);
//					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_trace));
//					mMoveMarker = aMap.addMarker(markerOptions);
//					mMoveMarker.setPosition(new LatLng(correctCoordinate[0], correctCoordinate[1]));
//					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(correctCoordinate[0], correctCoordinate[1]), 14)); 
//					 mMoveMarker.setTitle("设备信息");
//					 mMoveMarker.setSnippet(
//						"设备名称："+TrackApp.currentCar.name+"\n"+
//						"坐标："+lat+"%"+lon+"\n"+
//						"速度："+speed.substring(0,4)+"km/h"+"\n"+
//						"距离："+distant+"km"+"\n"+
//						"更新时间："+date+"\n"+
//						"电压："+voltage+"\n"+
//						"信号强度"+gsmStrength);
//					mMoveMarker.showInfoWindow();
//					timerStop();
//					dismissProgressDialog();
//			 }
//					
//         }else if (msg.what==ADDRESS_COMPLETE){
//        	 	
//         }
//         else if (msg.what==NetworkAdapter.MSG_FAIL){
//        	 timerStop();
//			dismissProgressDialog();
//        	 Toast.makeText(TraceActivity.this, "获取数据错误或数据库无数据",Toast.LENGTH_SHORT).show();
//         }else if (msg.what==TIME_OUT){
//        	 pd.dismiss();
//        	 Toast.makeText(TraceActivity.this, "设备关机或网络状况导致数据返回超时",Toast.LENGTH_SHORT).show();
//        	 return;
//         }
//        	
//        }
//        	
// };
 


	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				addressName = result.getRegeocodeAddress().getFormatAddress()
						+ "附近";
//				showMessage(addressName);
				TrackApp.currentCar.curAddress = addressName;
				
				
				handler.sendEmptyMessage(ADDRESS_COMPLETE);
			} else {
				Toast.makeText(TraceActivity.this, "无地址数据返回",Toast.LENGTH_SHORT).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(TraceActivity.this, "网络错误",Toast.LENGTH_SHORT).show();;
		} else if (rCode == 32) {
			Toast.makeText(TraceActivity.this, "秘钥错误",Toast.LENGTH_SHORT).show();;
		} else {
			Toast.makeText(TraceActivity.this, rCode,Toast.LENGTH_SHORT).show();;
		}
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_trace);
		mapView = (MapView) findViewById(R.id.map);
	    aMap = mapView.getMap();
		btMyPos=(Button) findViewById(R.id.bt_my_position);
		btDevPos=(Button) findViewById(R.id.bt_dev_position);
		btTrace=(Button) findViewById(R.id.bt_trace);
		
	    aMap.setOnMarkerClickListener(new OnMarkerClickListener(){

			@Override
			public boolean onMarkerClick(Marker m) {
				// TODO Auto-generated method stub
				m.showInfoWindow();
				return true;
			}
	    	
	    });
	    aMap.setOnMapClickListener(new OnMapClickListener(){

			
			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				mMoveMarker.hideInfoWindow();
			}
	    	
	    });
	    tbSatelite = (ToggleButton)findViewById(R.id.btTrace_Satelite);
	    tbSatelite.setOnClickListener(this);
	    MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.anchor(0.5f, 0.5f);
		if(TrackApp.currentCar.devtype.equals("GT601")){
			markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gt610));
		}else{
			markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_trace));
		}
		
		mMoveMarker = aMap.addMarker(markerOptions);
		mAMapLocationManager = LocationManagerProxy.getInstance(this);
		btMyPos.setOnClickListener(this);
		btDevPos.setOnClickListener(this);
		btTrace.setOnClickListener(this);
	
		
		//定位层设置
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
//		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(0.1f);// 设置圆形的边框粗细
		//aMap.getUiSettings().setMyLocationButtonEnabled(true);
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setMyLocationRotateAngle(180);
		aMap.setLocationSource(this);// 设置定位监听
		
		
		//地理转换初始化
		geocoderSearch = new GeocodeSearch(this); 
		geocoderSearch.setOnGeocodeSearchListener(this);
		
		//路径规划
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btTrace_Satelite:
			if(tbSatelite.isChecked()){
				aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
			}else{
				aMap.setMapType(AMap.MAP_TYPE_NORMAL);
			}
			break;
		case R.id.bt_dev_position:
			MsgEventHandler.c_sGetCarPosition(TrackApp.currentCar);
			
			showMessage("获取设备最新位置");
			if(TrackApp.currentCar.curState!=null){
				initPosition();
			}
			
			
			break;
		case R.id.bt_my_position:
			showProgressDialog("路径规划中，请稍后");
			aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
			//设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种 
			aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
			break;
		case R.id.bt_trace:
//			mAMapLocationManager.requestLocationData(
//					LocationProviderProxy.AMapNetwork, 2000, 10, this);
//			Thread t = new Thread(){
//				public void run(){
//					
//				}
//			};
//			handler.postDelayed(t, 12000);// 设置超过12秒还没有定位到就停止定位
//			
//			trackFlag = (trackFlag==0)?1:0;
//			if(trackFlag==0){
			polylineOptions = new PolylineOptions();
			polylineOptions.width(8);
			polylineOptions.color(Color.RED);
			showMessage("开始跟踪");
//			}else{
//				
//				poly.setVisible(false); 
//				poly.remove();
//				showMessage("取消跟踪");
//			}
//			
			
			break;
		}
	}
	
	
	

	private void initPosition(){
		if (TrackApp.currentCar.curState!=null){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			lat = TrackApp.currentCar.curState.gprmc.latitude;
			lon = TrackApp.currentCar.curState.gprmc.longitude;
			speed = TrackApp.currentCar.curState.gprmc.speed;
			distant = TrackApp.currentCar.curState.distant;
			if (TrackApp.currentCar.devtype.equals("MT400")){
				temperature  =  "0";
				accState = ByteHexUtil.getBooleanArray(TrackApp.currentCar.curState.portState[1])[1]?"开":"关";
			}else if(TrackApp.currentCar.devtype.equals("VT310")){
				temperature  =  TrackApp.currentCar.curState.temperature;
				accState = ByteHexUtil.getBooleanArray(TrackApp.currentCar.curState.portState[0])[3]?"开":"关";
			}else{
				temperature  =  "0";
				accState= "无状态";
			}
			
			date = format.format(new Date());
			
			gsmStrength = ""+ByteHexUtil.hexStringToBytes(TrackApp.currentCar.curState.gsmStrength)[0] ;
			voltage = TrackApp.currentCar.curState.voltage;
			DecimalFormat formatter = new DecimalFormat("##0.000000");
			double[] correctCoordinate = new double[2];
			
			double[] correctCoordinateLast = new double[2];
			GpsCorrect.transform(lat, lon, correctCoordinate);
			
			LatLng curLatLng = new  LatLng(correctCoordinate[0], correctCoordinate[1]);
			endpoint = new LatLonPoint(correctCoordinate[0], correctCoordinate[1]);
			
			if(TrackApp.currentCar.lastState!=null&&Double.parseDouble(TrackApp.currentCar.curState.gprmc.speed)>6){
				GpsCorrect.transform(TrackApp.currentCar.lastState.gprmc.latitude, TrackApp.currentCar.lastState.gprmc.longitude, correctCoordinateLast);
				LatLng lastLatLng = new  LatLng(correctCoordinateLast[0], correctCoordinateLast[1]);
				mMoveMarker.setRotateAngle((float) getAngle(lastLatLng,curLatLng));
			}
			
			mMoveMarker.setPosition(curLatLng);
			
			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(correctCoordinate[0], correctCoordinate[1]),18)); 
			mMoveMarker.setTitle("设备信息");
			mMoveMarker.setSnippet(
					"设备名称："+TrackApp.currentCar.name+"\n"+
					"坐标：纬度"+formatter.format(lat)+"经度"+formatter.format(lon)+"\n"+
					"速度："+speed+"km/h"+"\n"+
					"里程："+distant+"km"+"\n"+
					"更新时间："+date+"\n"+
					"温度："+temperature+"度\n"+
					"电压："+voltage+"V\n"+
					"ACC状态："+accState+"\n"+
					"信号强度:"+gsmStrength+"\n"+
					"地址："+formatAddress(addressName));
			mMoveMarker.showInfoWindow();
			LatLonPoint latLonPoint =new LatLonPoint(correctCoordinate[0], correctCoordinate[1]);
			getAddress(latLonPoint);
		}else{
			MsgEventHandler.c_sGetCarPosition(TrackApp.currentCar);
			showMessage("定位请求已发送");
			
		}
	}
	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}
	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		 if(msg.what==CNetworkAdapter.MSG_POSITION){
        	 Bundle b = msg.getData();
        	 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 String devid = b.getString("devid");
			 if(devid.equals(TrackApp.currentCar.devId)){
				 	initPosition();
				 	if(trackFlag == 0){
				 		double lat = TrackApp.currentCar.curState.gprmc.latitude;
				 		double[] correctCoordinate = new double[2];
						GpsCorrect.transform(lat, lon, correctCoordinate);
				 		polylineOptions.add(new LatLng(correctCoordinate[0], correctCoordinate[1]));
				 		poly = aMap.addPolyline(polylineOptions);
				 	}
//					 lat = b.getDouble("lat");
//		        	 lon = b.getDouble("lon");
//		        	 speed = b.getString("speed");
//		        	 distant = b.getString("ditant");
//					 date = format.format(new Date());
//					 voltage = b.getString("voltage");
//					 gsmStrength = b.getString("gsmStrength");
//					 double[] correctCoordinate = new double[2];
//					 GpsCorrect.transform(lat, lon, correctCoordinate);
//					 
//					 
////					 LatLonPoint latLonPoint =new LatLonPoint(correctCoordinate[0], correctCoordinate[1]);
////					 RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
////					 geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
//					 
//					
//						
//					
//				 	
//					mMoveMarker.setPosition(new LatLng(correctCoordinate[0], correctCoordinate[1]));
//					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(correctCoordinate[0], correctCoordinate[1]), 14)); 
//					mMoveMarker.setTitle("设备信息");
//					mMoveMarker.setSnippet(
//						"设备名称："+TrackApp.currentCar.name+"\n"+
//						"坐标：纬度"+Double.toString(lat).substring(0, 6)+"经度"+Double.toString(lon).substring(0, 6)+"\n"+
//						"速度："+speed.substring(0,4)+"km/h"+"\n"+
//						"里程："+distant+"km"+"\n"+
//						"更新时间："+date+"\n"+
//						"电压："+voltage+"\n"+
//						"信号强度"+gsmStrength);
//					
//					timerStop();
//					dismissProgressDialog();
			 }
					
         }else if (msg.what==ADDRESS_COMPLETE){
        	 DecimalFormat formatter = new DecimalFormat("##0.000000");
        	 mMoveMarker.setSnippet(
 					"设备名称："+TrackApp.currentCar.name+"\n"+
 					"坐标：纬度"+formatter.format(lat)+"经度"+formatter.format(lon)+"\n"+
 					"速度："+speed+"km/h"+"\n"+
 					"里程："+distant+"km"+"\n"+
 					"更新时间："+date+"\n"+
 					"温度："+temperature+"度\n"+
 					"电压："+voltage+"V\n"+
 					"ACC状态："+accState+"\n"+
 					"信号强度:"+gsmStrength+"\n"+
 					"地址："+addressName);
 			mMoveMarker.showInfoWindow();
         }
         else if (msg.what==NetworkAdapter.MSG_FAIL){
        	timerStop();
			dismissProgressDialog();
        	showMessage("获取数据错误或数据库无数据");
         }else if (msg.what==TIME_OUT){

        	return;
         }
	}

	
	
	
	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			
		}
		
		mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 2000, 10, this);
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

	@Override
	public void onLocationChanged(Location aLocation) {
		// TODO Auto-generated method stub
//		if (mListener != null && aLocation != null) {
//			//mListener.onLocationChanged(aLocation);// 显示系统小蓝点
//			LatLonPoint startpoint = new LatLonPoint(aLocation.getLatitude(), aLocation.getLongitude());// 定位雷达小图标
//			searchRouteResult(startpoint,endpoint);
//		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		// TODO Auto-generated method stub
		if (mListener != null && aLocation != null) {
			aMap.setMyLocationEnabled(false);
			LatLonPoint startpoint = new LatLonPoint(aLocation.getLatitude(), aLocation.getLongitude());// 定位雷达小图标
			searchRouteResult(startpoint,endpoint);
		
		}
	}

	
	
	/**
	 * 根据两点算取图标转的角度
	 */
	private double getAngle(LatLng fromPoint, LatLng toPoint) {
		double slope = getSlope(fromPoint, toPoint);
		if (slope == Double.MAX_VALUE) {
			if (toPoint.latitude > fromPoint.latitude) {
				return 0;
			} else {
				return 180;
			}
		}
		float deltAngle = 0;
		if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
			deltAngle = 180;
		}
		double radio = Math.atan(slope);
		double angle = 180 * (radio / Math.PI) + deltAngle - 90;
		return angle;
	}
	
	/**
	 * 算斜率
	 */
	private double getSlope(LatLng fromPoint, LatLng toPoint) {
		if (toPoint.longitude == fromPoint.longitude) {
			return Double.MAX_VALUE;
		}
		double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
		return slope;

	}
	
	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		LatLonPoint l = new LatLonPoint(22.601888190152657 ,114.05940618447902);
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				l, endPoint);
		
		DriveRouteQuery query = new DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault,
				null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
		routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		
	}
	
	
	

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		// TODO Auto-generated method stub
		dismissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						this, aMap, drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				showMessage("没有路径结果");
			}
		} else if (rCode == 27) {
			showMessage("网络错误");
		} else if (rCode == 32) {
			showMessage("秘钥错误");
		} else {
			showMessage("其他错误");
		}
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		// TODO Auto-generated method stub
		
	}

	
	
	private String formatAddress(String address){
		String res="";
		int length = 15;
		int a = address.length()/length;
		
		for (int i=0; i<a; i++){
			res = res+address.substring(i*15,i*15+15)+"\n";
		}
		
		
		return address;
		
	}
	
//	/**
//	 * 循环进行移动逻辑
//	 */
//	public void moveLooper() {
//		new Thread() {
//
//			public void run() {
//				while (true) {
//					
//						for (int i = 0; i < mVirtureRoad.getPoints().size() - 1; i++) {
//
//							
//							LatLng startPoint = mVirtureRoad.getPoints().get(i);
//							LatLng endPoint = mVirtureRoad.getPoints().get(i + 1);
//							mMoveMarker.setPosition(startPoint);
//
//							mMoveMarker.setRotateAngle((float) getAngle(startPoint,
//									endPoint));
//
//							double slope = getSlope(startPoint, endPoint);
//							//是不是正向的标示（向上设为正向）
//							boolean isReverse = (startPoint.latitude > endPoint.latitude);
//
//							double intercept = getInterception(slope, startPoint);
//
//							double xMoveDistance = isReverse ? getXMoveDistance(slope)
//									: -1 * getXMoveDistance(slope);
//
//							
//							for (double j = startPoint.latitude;!((j > endPoint.latitude)^ isReverse);j = j-xMoveDistance) {
//								LatLng latLng = null;
//								if (slope != Double.MAX_VALUE) {
//									latLng = new LatLng(j, (j - intercept) / slope);
//								} else {
//									latLng = new LatLng(j, startPoint.longitude);
//								}
//								mMoveMarker.setPosition(latLng);
//								
//								
//								
//								try {
//									Thread.sleep(TIME_INTERVAL);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}catch(Exception e){
//									e.printStackTrace();
//								}
//							}
//						}
//					
//
//					}
//				}
//			
//
//		}.start();
//	}
}
