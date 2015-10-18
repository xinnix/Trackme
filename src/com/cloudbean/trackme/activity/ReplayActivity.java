package com.cloudbean.trackme.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackerUtil.DateTimeUtil;
import com.cloudbean.trackerUtil.GpsCorrect;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.R.drawable;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;
import com.cloudbean.trackme.TrackApp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ReplayActivity extends BaseActivity {

	private MapView mMapView;
	private AMap mAmap;
	private Polyline mVirtureRoad;
	private Marker mMoveMarker;
	private ToggleButton tbSatelite = null;
	private Button tbPlay = null;
	private Button tbStop = null;
	private SeekBar sbSpeed = null;
	private GridLayout la = null;
	private String isTimeSelected =null;
	
	private int btFlag = 1;
	private int isSameDay=0;
	
	private int carId;
	private String startDate;
	private String endDate;
	
	private static int REPLAY_COMPLETE = 0x8888;
	
	// 通过设置间隔时间和距离可以控制速度和图标移动的距离
	private static int TIME_INTERVAL = 300;
	private static final double DISTANCE = 0.0001;
	private Track[] trackList = null;
	
	//超时相关控制
	MyThread moveThread= null ;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMapView.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		carId = intent.getIntExtra("carId", 0);
		startDate = intent.getStringExtra("startDate");
		endDate = intent.getStringExtra("endDate");
		
 		if(DateTimeUtil.isSameDay(startDate, endDate)){
 			MsgEventHandler.sGetCarTrack(carId,endDate,startDate);
 		
 		}else{
 			isSameDay = 1;
 			MsgEventHandler.sGetCarTrack(carId,endDate,DateTimeUtil.getEndTimeOfTheDay(endDate));
 		}
		
		showProgressDialog("历史轨迹获取中...");
		timerStart();
		
		
		//mAmap.moveCamera(CameraUpdateFactory.zoomTo(10));
		
	}
	

//	private  Handler handler = new Handler() {  
//	        @Override  
//	        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
//	         if(msg.what==NetworkAdapter.MSG_TRACK){
//	        	 dismissProgressDialog();
//	        	 timerStop();
//	        	 Bundle b = msg.getData();
//	        	 Track[] trackList = (Track[]) b.getParcelableArray("trackList");
//	        	 initRoadData(trackList);
//			     if (mVirtureRoad.getPoints().size()>1){
//			        moveThread.start();
//			        showMessage("单击播放按钮回放历史轨迹");
//			     }else{
//			    	 showMessage("这段时间处于停车状态");
//			        return;
//			      }
//		    		// 关闭ProgressDialog
//		    		
//	         }else if (msg.what==NetworkAdapter.MSG_FAIL){
//	        	 dismissProgressDialog();
//	        	 timerStop();
//	        	 showMessage("获取数据错误或数据库无数据");
//	         }else if (msg.what==TIME_OUT){
//	        	 dismissProgressDialog();
//	        	 showMessage("设备关机或网络状况导致数据返回超时");
//	        	 return;
//	         }
//	       }
//	        	
//	 };
	private void initRoadData(Track[] tracklist) {
		if (tracklist.length>0){
			double[] correctCoordinate = new double[2];
			GpsCorrect.transform(tracklist[0].latitude, tracklist[0].longitude, correctCoordinate);
			mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(correctCoordinate[0], correctCoordinate[1]), 18));
			int num=tracklist.length;
	    	PolylineOptions polylineOptions = new PolylineOptions();
	    	MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.anchor(0.5f, 0.5f);
	    	/*
			 * 判断停车情况
			 */
			List<Track> stopPointList = new  ArrayList<Track>(); 
	    	if(TrackApp.currentCar.devtype.equals("GT601")){
	    		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gt610_r));
	    		for(int i=0;i<num;i++)
				{
	    			GpsCorrect.transform(tracklist[i].latitude, tracklist[i].longitude, correctCoordinate);
	    			MarkerOptions point = new MarkerOptions();
					point.icon(BitmapDescriptorFactory.fromResource(R.drawable.point_marker));
			    	point.position(new LatLng(correctCoordinate[0], correctCoordinate[1]));
			    	mAmap.addMarker(point);
	    			polylineOptions.add(new LatLng(correctCoordinate[0], correctCoordinate[1]));
				}
	    	}else{
	    		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_track));
				
		    	for(int i=0;i<num;i++)
				{
		    		if(tracklist[i].isLocated){
						if(tracklist[i].speed<6){
//							GpsCorrect.transform(tracklist[i].latitude, tracklist[i].longitude, correctCoordinate);
							 //polylineOptions.add(new LatLng(correctCoordinate[0], correctCoordinate[1]));
							stopPointList.add(tracklist[i]);
							
						}else{
							if(stopPointList.size()>1){
								String startStop =  stopPointList.get(0).sdate;
								String endStop =  stopPointList.get(stopPointList.size()-1).sdate;
								if(DateTimeUtil.minBetweenPoint(startStop,endStop)>10){
									 MarkerOptions mo = new MarkerOptions();
									 mo.anchor(0.5f, 0.5f);
									 mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.parking));
									 GpsCorrect.transform(stopPointList.get(0).latitude, stopPointList.get(0).longitude, correctCoordinate);
									 mo.position(new LatLng(correctCoordinate[0], correctCoordinate[1]));
									 mAmap.addMarker(mo);
								}
								stopPointList= new  ArrayList<Track>();
							}
							GpsCorrect.transform(tracklist[i].latitude, tracklist[i].longitude, correctCoordinate);
							MarkerOptions point = new MarkerOptions();
							point.icon(BitmapDescriptorFactory.fromResource(R.drawable.point_marker));
					    	point.position(new LatLng(correctCoordinate[0], correctCoordinate[1]));
					    	mAmap.addMarker(point);
							polylineOptions.add(new LatLng(correctCoordinate[0], correctCoordinate[1]));
							
						}
		    		}
				}
	    		
	    	}
	    	if(stopPointList.size()>1){
				String startStop =  stopPointList.get(0).sdate;
				String endStop =  stopPointList.get(stopPointList.size()-1).sdate;
				if(DateTimeUtil.minBetweenPoint(startStop,endStop)>10){
					 MarkerOptions mo = new MarkerOptions();
					 mo.anchor(0.5f, 0.5f);
					 mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.parking));
					 GpsCorrect.transform(stopPointList.get(0).latitude, stopPointList.get(0).longitude, correctCoordinate);
					 mo.position(new LatLng(correctCoordinate[0], correctCoordinate[1]));
					 mAmap.addMarker(mo);
				}
				stopPointList= new  ArrayList<Track>();
			}
			polylineOptions.width(8);
			polylineOptions.color(Color.DKGRAY);
			polylineOptions.setDottedLine(true);
			mVirtureRoad = mAmap.addPolyline(polylineOptions);
			Log.i("track", ""+mVirtureRoad.getPoints().size());

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
		if(moveThread == null){
			moveThread=new MyThread();
			
		}else{
//			moveThread.stop = false;
//			moveThread.setSuspend(true);
//			moveThread.start();
		}
		
 		
	
		
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		
		if(moveThread!=null){
			if(moveThread.isSuspend()){
				
				
			}else{
				moveThread.setSuspend(true);
				moveThread.interrupt();
				tbPlay.setText("播放");
			}
		}
		
	}
	
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		if(moveThread!=null){
			if(moveThread.isSuspend()){
				moveThread.stop = true;
				moveThread.interrupt();
			}else{
				moveThread.interrupt();
				moveThread.stop = true;
				moveThread.interrupt();
			}
		}
		
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
	 * 计算x方向每次移动的距离
	 */
	private double getXMoveDistance(double slope) {
		double dis=0;
		if (slope == Double.MAX_VALUE) {
			dis=DISTANCE;
		}else{
			dis = Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
			if(dis == 0){
				dis=DISTANCE;
			}
		}
		
		
		return dis;
	}
	
	
	

	/**
	 * 循环进行移动逻辑
	 */
	
	class MyThread extends Thread{
		private boolean suspend = true; 
		private String control = ""; // 只是需要一个对象而已，这个对象没有实际意义  
		private int progress = 0;
		private boolean stop = false;
//		public void setControl(int c){
//			control=c;
//		}
		public void setSuspend(boolean suspend) {  
	        if (!suspend) {  
		            synchronized (control) {  
		                control.notifyAll();  
		            }  
		        }  
		        this.suspend = suspend;  
		}
		
		public boolean isSuspend() {  
	        return this.suspend;  
	    }
		
		public void setProgress(int c){
			progress=c;
		}
		public int getProgress(){
			return progress;
		}
		
		public void run() {  
	        while (!stop) {  
		            synchronized (control) {  
		                if (suspend) {  
		                    try {  
		                        control.wait();  
		                    } catch (InterruptedException e) {  
		                        e.printStackTrace();
		                        break;
		                    }  
		            }  
		             
		            for (; progress < mVirtureRoad.getPoints().size() - 1;) {
		
						LatLng startPoint = mVirtureRoad.getPoints().get(progress);
						LatLng endPoint = mVirtureRoad.getPoints().get(progress + 1);
						mMoveMarker.setPosition(startPoint);
			
						mMoveMarker.setRotateAngle((float) getAngle(startPoint,endPoint));
						
						double slope = getSlope(startPoint, endPoint);
						//是不是正向的标示（向上设为正向）
						boolean isReverse = (startPoint.latitude > endPoint.latitude);
			
						double intercept = getInterception(slope, startPoint);
			
						double xMoveDistance = isReverse ? getXMoveDistance(slope)
								: -1 * getXMoveDistance(slope);
			
						
						for (double j = startPoint.latitude;!((j > endPoint.latitude)^ isReverse);j = j-xMoveDistance) {
							LatLng latLng = null;
							if (slope != Double.MAX_VALUE) {
								latLng = new LatLng(j, (j - intercept) / slope);
							} else {
								latLng = new LatLng(j, startPoint.longitude);
							}
							mMoveMarker.setPosition(latLng);
							mAmap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
	
						}
						progress++;
						
						
						try {
							Thread.sleep(TIME_INTERVAL);
						}catch (InterruptedException e) {
							e.printStackTrace();
							break;
						}catch(Exception e){
							e.printStackTrace();
						}
						
					
		            }
		            
		            if(progress == mVirtureRoad.getPoints().size() - 1){
		            	//showMessage("播放完毕");
		            	progress = 0;
		            	this.setSuspend(true);
		            	TrackApp.curHandler.sendEmptyMessage(REPLAY_COMPLETE);
		            	
		            }
		            
		            
		          }
		          
		        
	        }
	        
	    }  

	}
	//画图线程

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_reply);
		mMapView = (MapView) findViewById(R.id.replymap);	
		mAmap = mMapView.getMap();
		//mAmap.setPointToCenter((int) (22.90923 * 1E6), (int) (116.397428 * 1E6));
		tbSatelite = (ToggleButton)findViewById(R.id.btSatelite);
		tbPlay = (Button)findViewById(R.id.btPlay);
		tbStop = (Button)findViewById(R.id.btStop);
		sbSpeed = (SeekBar)findViewById(R.id.speedSeekBar);
		sbSpeed.setMax(1000);
		
		sbSpeed.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar sb) {
				// TODO Auto-generated method stub
				TIME_INTERVAL=sb.getProgress()+100;
			}
			
		});
		
		tbPlay.setOnClickListener(this);
		tbStop.setOnClickListener(this);
		
		
		
		
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btPlay:
			
			if(tbPlay.getText().toString().equals("播放")){
				moveThread.setSuspend(false);
				tbPlay.setText("暂停");
			}else{
				moveThread.setSuspend(true);
				moveThread.interrupt();
				tbPlay.setText("播放");
			}
			break;
//		case R.id.btStop:
//			moveThread.setSuspend(true);
//			moveThread.interrupt();
//			break;
		case R.id.btSatelite:
			if(tbSatelite.isChecked()){
				mAmap.setMapType(AMap.MAP_TYPE_SATELLITE);
			}else{
				mAmap.setMapType(AMap.MAP_TYPE_NORMAL);
			}
			break;
		}
	}

	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		
		timerStop();
      	Bundle b = msg.getData();
		if(msg.what==NetworkAdapter.MSG_TRACK){
       	 if(isSameDay==0){
       		if(trackList==null){
       			trackList = (Track[]) b.getParcelableArray("trackList");
       		}else{
       			Track[] tmpTrackList1 = (Track[]) b.getParcelableArray("trackList");
       			Track[] totalTrackList = new Track[trackList.length+tmpTrackList1.length];
       			System.arraycopy(trackList, 0, totalTrackList, 0, trackList.length);
       			System.arraycopy(tmpTrackList1, 0, totalTrackList, trackList.length, tmpTrackList1.length);
       			trackList = totalTrackList;
       		}
       		
       	 }else if(isSameDay==1){
       		trackList = (Track[]) b.getParcelableArray("trackList");
       		isSameDay--;
       		MsgEventHandler.sGetCarTrack(carId,DateTimeUtil.getStartTimeOfTheDay(startDate),startDate);
       		return;
       	 }
       	 
//       	 Track[] trackList = (Track[]) b.getParcelableArray("trackList");
       	 initRoadData(trackList);
		     if (mVirtureRoad.getPoints().size()>1){
		    	
		    	 moveThread.start();
//		    	 moveThread.start();
		        showMessage("单击播放按钮回放历史轨迹");
		     }else{
		    	showMessage("这段时间处于停车状态");
		       
		     }
	    		// 关闭ProgressDialog
		     dismissProgressDialog();	
        }else if (msg.what==NetworkAdapter.MSG_FAIL){
        	
       	 	dismissProgressDialog();
       	 	timerStop();
       	 	b = msg.getData();
       	 	String reason = b.getString("reason");
       	 	if(trackList==null&&isSameDay==0){
       	 		showMessage(reason);
       	 	}
       	 	
        }else if (msg.what == REPLAY_COMPLETE){
        	tbPlay.setText("播放");
        }else if (msg.what==TIME_OUT){
	       	 dismissProgressDialog();
	       	 showMessage("设备关机或网络状况导致数据返回超时");
	       	 return;
        }
     }
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event)
//	{
//		if (keyCode == KeyEvent.KEYCODE_BACK )
//		{
//			
//				moveThread.interrupt();
//			
//	
//		}
//		
//		return super.onKeyDown(keyCode, event);
//		
//	}



	

}
