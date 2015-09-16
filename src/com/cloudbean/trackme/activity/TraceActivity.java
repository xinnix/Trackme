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
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
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

public class TraceActivity extends BaseActivity implements OnGeocodeSearchListener,LocationSource,AMapLocationListener {
	
	private MapView mapView = null;
    private AMap aMap = null;
    private Marker mMoveMarker = null;
	private ProgressDialog pd = null;
	private ToggleButton tbSatelite = null;
	private String addressName = null;
	private GeocodeSearch geocoderSearch = null;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	
	
	private final int ADDRESS_COMPLETE = 0x3001;
	
	
	private Button btMyPos;
	private Button btDevPos;
	private Button btTrace;
	private Button btNavi;
	
	
	
	double lat;
	double lon;
	String speed;
	String distant;
	String date;
	String temperature;
	String gsmStrength;
	String accState;
	String voltage;
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
		if(TrackApp.currentCar.lastState==null){
			MsgEventHandler.c_sGetCarPosition(TrackApp.currentCar);
			showMessage("定位请求已发送");
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

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
		btNavi=(Button) findViewById(R.id.bt_navi);
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
		btNavi.setOnClickListener(this);
		
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
			showMessage("定位请求已发送");
			
			break;
		case R.id.bt_my_position:
			
			aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
			aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);//设置定位类型
			break;
		case R.id.bt_trace:
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
			Thread t = new Thread(){
				public void run(){
					
				}
			};
			handler.postDelayed(t, 12000);// 设置超过12秒还没有定位到就停止定位
			break;
		case R.id.bt_navi:
			
			break;
		}
	}
	
	
	

	private void initPosition(){
		if (TrackApp.currentCar.lastState!=null){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			lat = TrackApp.currentCar.lastState.gprmc.latitude;
			lon = TrackApp.currentCar.lastState.gprmc.longitude;
			speed = TrackApp.currentCar.lastState.gprmc.speed;
			distant = TrackApp.currentCar.lastState.distant;
			if (TrackApp.currentCar.devtype.equals("MT400")){
				temperature  =  "无温度";
				accState = ByteHexUtil.getBooleanArray(TrackApp.currentCar.lastState.portState[1])[1]?"开":"关";
			}else if(TrackApp.currentCar.devtype.equals("VT310")){
				temperature  =  TrackApp.currentCar.lastState.temperature;
				accState = ByteHexUtil.getBooleanArray(TrackApp.currentCar.lastState.portState[0])[3]?"开":"关";
			}else{
				temperature  =  "无温度";
				accState= "无状态";
			}
			
			date = format.format(new Date());
			
			gsmStrength = ""+ByteHexUtil.hexStringToBytes(TrackApp.currentCar.lastState.gsmStrength)[0] ;
			voltage = TrackApp.currentCar.lastState.voltage;
			DecimalFormat formatter = new DecimalFormat("##0.000000");
			double[] correctCoordinate = new double[2];
			GpsCorrect.transform(lat, lon, correctCoordinate);
			mMoveMarker.setPosition(new LatLng(correctCoordinate[0], correctCoordinate[1]));
			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(correctCoordinate[0], correctCoordinate[1]), 14)); 
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
				"信号强度:"+gsmStrength);
			mMoveMarker.showInfoWindow();
		}else{
			MsgEventHandler.c_sGetCarPosition(TrackApp.currentCar);
			showMessage("定位请求已发送");
			
		}
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
					timerStop();
					dismissProgressDialog();
			 }
					
         }else if (msg.what==ADDRESS_COMPLETE){
        	 	
         }
         else if (msg.what==NetworkAdapter.MSG_FAIL){
        	timerStop();
			dismissProgressDialog();
        	showMessage("获取数据错误或数据库无数据");
         }else if (msg.what==TIME_OUT){

        	return;
         }
	}

	
	
	private void setUpMap() {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		//设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种 
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
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
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
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
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
		
		}
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
