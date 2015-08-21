package com.cloudbean.trackme;

import java.util.Timer;
import java.util.TimerTask;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class TraceActivity extends Activity implements OnGeocodeSearchListener {
	
	private MapView mapView;
    private AMap aMap;
    TrackApp ta  = null;
    private Marker mMoveMarker = null;
	private ProgressDialog pd = null;
	private ToggleButton tbSatelite = null;
	private String addressName;
	private GeocodeSearch geocoderSearch;
	
	private final int ADDRESS_COMPLETE = 0x3001;
	
	//超时相关控制
	private Timer timer = null;
	private static final int TIME_LIMIT = 30000;
	
	
	double lat;
	double lon;
	String speed;
	String distant;
	String date;
	String voltage;
	String gsmStrength;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);
		mapView = (MapView) findViewById(R.id.map);
		
		mapView.onCreate(savedInstanceState);// 必须要写
	    aMap = mapView.getMap();
	    tbSatelite = (ToggleButton)findViewById(R.id.btTrace_Satelite);
//	    geocoderSearch = new GeocodeSearch(this);
//		geocoderSearch.setOnGeocodeSearchListener(this);
	    
	    
	    tbSatelite.setOnClickListener(new OnClickListener(){
	    
			@Override
			public void onClick(View v) {
				if(tbSatelite.isChecked()){
					aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
				}else{
					aMap.setMapType(AMap.MAP_TYPE_NORMAL);
				}
				
			}
			
		});
	
	    
	   
	}

	protected void onResume() {
		super.onResume();
		mapView.onResume();
		
		ta = (TrackApp)getApplication();
	    ta.setHandler(handler);
		MsgEventHandler.c_sGetCarPosition(ta.currentCar);	
		pd = new ProgressDialog(TraceActivity.this);
		pd.setMessage("定位中...");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				handler.sendEmptyMessage(TIME_LIMIT);	
			}
			
		}, TIME_LIMIT);
	
	    
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
	
	
    private  Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
         if(msg.what==CNetworkAdapter.MSG_POSITION){
        	 Bundle b = msg.getData();
        	 
			 String devid = b.getString("devid");
			 if(devid.equals(ta.currentCar.devId)){
					 lat = b.getDouble("lat");
		        	 lon = b.getDouble("lon");
		        	 speed = b.getString("speed");
		        	 distant = b.getString("ditant");
					 date = b.getString("date");
					 voltage = b.getString("voltage");
					 gsmStrength = b.getString("gsmStrength");
					 double[] correctCoordinate = new double[2];
					 GpsCorrect.transform(lat, lon, correctCoordinate);
					 
					 
//					 LatLonPoint latLonPoint =new LatLonPoint(correctCoordinate[0], correctCoordinate[1]);
//					 RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//					 geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
					 
					
						
					
				 	MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.anchor(0.5f, 0.5f);
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
					mMoveMarker = aMap.addMarker(markerOptions);
					mMoveMarker.setPosition(new LatLng(correctCoordinate[0], correctCoordinate[1]));
					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(correctCoordinate[0], correctCoordinate[1]), 14)); 
					 mMoveMarker.setTitle("设备信息");
					 mMoveMarker.setSnippet("坐标："+lat+"%"+lon+"\n"+
						"速度:"+speed+"  距离"+distant+"\n"+
						"更新时间："+date+"\n"+
						"电压："+voltage+"\n"+
						"信号强度"+gsmStrength);
					mMoveMarker.showInfoWindow();
					timer.cancel();
					pd.dismiss();// 关闭ProgressDialog 
			 }
					
         }else if (msg.what==ADDRESS_COMPLETE){
        	 	
         }
         else if (msg.what==NetworkAdapter.MSG_FAIL){
        	 timer.cancel();
        	 pd.dismiss();
        	 Toast.makeText(TraceActivity.this, "获取数据错误或数据库无数据",Toast.LENGTH_SHORT).show();
         }else if (msg.what==TIME_LIMIT){
        	 pd.dismiss();
        	 Toast.makeText(TraceActivity.this, "设备关机或网络状况导致数据返回超时",Toast.LENGTH_SHORT).show();
        	 return;
         }
        	
        }
        	
 };
 



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
