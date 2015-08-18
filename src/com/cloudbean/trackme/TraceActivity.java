package com.cloudbean.trackme;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
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

public class TraceActivity extends Activity {
	
	private MapView mapView;
    private AMap aMap;
    TrackApp ta  = null;
    private Marker mMoveMarker = null;
	private ProgressDialog pd = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);
		mapView = (MapView) findViewById(R.id.map);
	    mapView.onCreate(savedInstanceState);// 必须要写
	    aMap = mapView.getMap();
	    
	    
	    ta = (TrackApp)getApplication();
	    
	    
	    
	    
	   
	}

	protected void onResume() {
		super.onResume();
		ta = (TrackApp)getApplication();
	    ta.setHandler(handler);
	    mapView.onResume();
		
		
		MsgEventHandler.c_sGetCarPosition(ta.currentCar);
		
		pd = new ProgressDialog(TraceActivity.this);
		pd.setMessage("定位中...");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	
	    
	}
	
	 
    private  Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
         if(msg.what==CNetworkAdapter.MSG_POSITION){
        	 Bundle b = msg.getData();
        	 
			 String devid = b.getString("devid");
			 if(devid.equals(ta.currentCar.devId)){
					 double lat = b.getDouble("lat");
		        	 double lon = b.getDouble("lon");
		        	 String speed = b.getString("speed");
		        	 String distant = b.getString("ditant");
					 String date = b.getString("date");
				 	MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.anchor(0.5f, 0.5f);
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
					mMoveMarker = aMap.addMarker(markerOptions);
					double[] correctCoordinate = new double[2];
					GpsCorrect.transform(lat, lon, correctCoordinate);
					mMoveMarker.setPosition(new LatLng(correctCoordinate[0], correctCoordinate[1]));
					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(correctCoordinate[0], correctCoordinate[1]), 14));
					pd.dismiss();// 关闭ProgressDialog     
				 
			 }
					
         }else if (msg.what==NetworkAdapter.MSG_FAIL){
        	 pd.dismiss();
        	 Toast.makeText(TraceActivity.this, "获取数据错误或数据库无数据",Toast.LENGTH_SHORT).show();
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
