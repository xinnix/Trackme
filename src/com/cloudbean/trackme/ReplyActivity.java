package com.cloudbean.trackme;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.cloudbean.model.Car;
import com.cloudbean.model.Login;
import com.cloudbean.model.Track;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackerUtil.GpsCorrect;
import com.cloudbean.trackerUtil.MsgEventHandler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ReplyActivity extends Activity {
	private TrackApp ta = null ;
	private MapView mMapView;
	private AMap mAmap;
	private Polyline mVirtureRoad;
	private Marker mMoveMarker;
	private ProgressDialog pd = null;
	// 通过设置间隔时间和距离可以控制速度和图标移动的距离
	private static final int TIME_INTERVAL = 80;
	private static final double DISTANCE = 0.0001;
	private Track[] trackList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply);
		mMapView = (MapView) findViewById(R.id.replymap);

		mMapView.onCreate(savedInstanceState);
		mAmap = mMapView.getMap();
		//mAmap.setPointToCenter((int) (22.90923 * 1E6), (int) (116.397428 * 1E6));
		
		Intent intent = this.getIntent();
		int carId = intent.getIntExtra("carId", 0);
		String startDate = intent.getStringExtra("startDate");
		String endDate = intent.getStringExtra("endDate");
		
		MsgEventHandler.sGetCarTrack(carId,startDate,endDate);
		
		pd = new ProgressDialog(ReplyActivity.this);
		pd.setMessage("历史轨距获取中...");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		
		//mAmap.moveCamera(CameraUpdateFactory.zoomTo(10));
		
	}
	

	private  Handler handler = new Handler() {  
	        @Override  
	        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
	         if(msg.what==NetworkAdapter.MSG_SUCCESS){
	        	 Bundle b = msg.getData();
		        	Track[] trackList = (Track[]) b.getParcelableArray("trackList");
		        	initRoadData(trackList);
		        	pd.dismiss();// 关闭ProgressDialog
		        	if (mVirtureRoad.getPoints().size()>0){
		        		moveLooper();
		        	}else{
		        		Toast.makeText(getApplicationContext(), "无数据或处于停车状态",Toast.LENGTH_SHORT).show();
		        	}
		    		
	         }else{
	        	 pd.dismiss();// 关闭ProgressDialog
	        	 Toast.makeText(getApplicationContext(), "获取数据错误或数据库无数据",Toast.LENGTH_SHORT).show();
	         }
	        	
	        }
	        	
	 };
	private void initRoadData(Track[] tracklist) {
		// 116.504505 39.931057
	//	double centerLatitude = 39.916049;
	//	double centerLontitude = 116.399792;
		if (tracklist.length>0){
			double[] correctCoordinate = new double[2];
			GpsCorrect.transform(tracklist[0].latitude, tracklist[0].longitude, correctCoordinate);
			mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(correctCoordinate[0], correctCoordinate[1]), 14));
			int num=tracklist.length;
//			if(num>10)
//			{
//				
//			}
//			double centerLatitude = correctCoordinate[0];
//			double centerLontitude = correctCoordinate[1];
//			double deltaAngle = Math.PI / 180 * 5;
//			double radius = 0.02;
	    	PolylineOptions polylineOptions = new PolylineOptions();
//			for (double i = 0; i < Math.PI * 2; i = i + deltaAngle) {
//				float latitude = (float) (-Math.cos(i) * radius + centerLatitude);
//				float longtitude = (float) (Math.sin(i) * radius + centerLontitude);
//				polylineOptions.add(new LatLng(latitude, longtitude));
//				if (i > Math.PI) {
//					deltaAngle = Math.PI / 180 * 30;
//				}
//			}
//			float latitude = (float) (-Math.cos(0) * radius + centerLatitude);
//			float longtitude = (float) (Math.sin(0) * radius + centerLontitude);
//			polylineOptions.add(new LatLng(latitude, longtitude));
	    	//new LatLng(socket9.weidu.get(0),socket9.jingdu.get(0))
			int stopFlag = 1;
	    	for(int i=0;i<num;i++)
			{
				if(tracklist[i].status.equals(Track.ACC_SHUTDOWN)&&tracklist[i].isLocated&&(stopFlag==1)){
					GpsCorrect.transform(tracklist[i].latitude, tracklist[i].longitude, correctCoordinate);
					 polylineOptions.add(new LatLng(correctCoordinate[0], correctCoordinate[1]));
					 MarkerOptions markerOptions = new MarkerOptions();
					 markerOptions.anchor(0.5f, 0.5f);
					 markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.carstop));
					 int last = polylineOptions.getPoints().size()-1;
					 markerOptions.position(polylineOptions.getPoints().get(last));
					 mAmap.addMarker(markerOptions);
					 stopFlag=0;
				}
				if (tracklist[i].status.equals(Track.ACC_START)&&tracklist[i].isLocated){
					GpsCorrect.transform(tracklist[i].latitude, tracklist[i].longitude, correctCoordinate);
					 polylineOptions.add(new LatLng(correctCoordinate[0], correctCoordinate[1]));
					 stopFlag=1;
				}
				
			}
			

			polylineOptions.width(8);
			polylineOptions.color(Color.RED);
			mVirtureRoad = mAmap.addPolyline(polylineOptions);
			Log.i("track", ""+mVirtureRoad.getPoints().size());
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.anchor(0.5f, 0.5f);
			markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
			
			//markerOptions.position(polylineOptions.getPoints().get(0));
			mMoveMarker = mAmap.addMarker(markerOptions);
			if(polylineOptions.getPoints().size()>1){
				mMoveMarker.setRotateAngle((float) getAngle(0));
			}
			
		}else{
			Toast.makeText(this,"没有轨迹记录", Toast.LENGTH_SHORT).show();
		}
		

	}

	/**
	 * 根据点获取图标转的角度
	 */
	private double getAngle(int startIndex) {
		if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
		LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
		return getAngle(startPoint, endPoint);
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
	 * 根据点和斜率算取截距
	 */
	private double getInterception(double slope, LatLng point) {

		double interception = point.latitude - slope * point.longitude;
		return interception;
	}

	/**
	 * 算取斜率
	 */
	private double getSlope(int startIndex) {
		if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
		LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
		return getSlope(startPoint, endPoint);
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
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
		ta = (TrackApp)getApplication();
	    ta.setHandler(handler);
	    Log.i("test", "onResume");
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	/**
	 * 计算x方向每次移动的距离
	 */
	private double getXMoveDistance(double slope) {
		if (slope == Double.MAX_VALUE) {
			return DISTANCE;
		}
		return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
	}

	/**
	 * 循环进行移动逻辑
	 */
	public void moveLooper() {
		new Thread() {

			public void run() {
				while (true) {
					
						for (int i = 0; i < mVirtureRoad.getPoints().size() - 1; i++) {

							
							LatLng startPoint = mVirtureRoad.getPoints().get(i);
							LatLng endPoint = mVirtureRoad.getPoints().get(i + 1);
							mMoveMarker.setPosition(startPoint);

							mMoveMarker.setRotateAngle((float) getAngle(startPoint,
									endPoint));

							double slope = getSlope(startPoint, endPoint);
							//是不是正向的标示（向上设为正向）
							boolean isReverse = (startPoint.latitude > endPoint.latitude);

							double intercept = getInterception(slope, startPoint);

							double xMoveDistance = isReverse ? getXMoveDistance(slope)
									: -1 * getXMoveDistance(slope);

							
							for (double j = startPoint.latitude;
									!((j > endPoint.latitude)^ isReverse);
									
									j = j
									- xMoveDistance) {
								LatLng latLng = null;
								if (slope != Double.MAX_VALUE) {
									latLng = new LatLng(j, (j - intercept) / slope);
								} else {
									latLng = new LatLng(j, startPoint.longitude);
								}
								mMoveMarker.setPosition(latLng);
								
								
								try {
									Thread.sleep(TIME_INTERVAL);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					

					}
				}
			

		}.start();
	}


	

}
