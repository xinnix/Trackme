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
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.cloudbean.model.Car;
import com.cloudbean.model.Login;
import com.cloudbean.model.Track;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackerUtil.GpsCorrect;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ReplayActivity extends Activity {
	private TrackApp ta = null ;
	private MapView mMapView;
	private AMap mAmap;
	private Polyline mVirtureRoad;
	private Marker mMoveMarker;
	private ProgressDialog pd = null;
	private ToggleButton tbSatelite = null;
	private Button tbPlay = null;
	private Button tbStop = null;
	private SeekBar sbSpeed = null;
	// ͨ�����ü��ʱ��;�����Կ����ٶȺ�ͼ���ƶ��ľ���
	private static int TIME_INTERVAL = 200;
	private static final double DISTANCE = 0.0001;
	private Track[] trackList = null;
	
	//��ʱ��ؿ���
	private Timer timer = null;
	private static final int TIME_LIMIT = 8000;
	MyThread moveThread= null ;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply);
		mMapView = (MapView) findViewById(R.id.replymap);

		mMapView.onCreate(savedInstanceState);
		mAmap = mMapView.getMap();
		//mAmap.setPointToCenter((int) (22.90923 * 1E6), (int) (116.397428 * 1E6));
		tbSatelite = (ToggleButton)findViewById(R.id.btSatelite);
		tbPlay = (Button)findViewById(R.id.btPlay);
		tbStop = (Button)findViewById(R.id.btStop);
		sbSpeed = (SeekBar)findViewById(R.id.speedSeekBar);
		
		
		Intent intent = this.getIntent();
		int carId = intent.getIntExtra("carId", 0);
		String startDate = intent.getStringExtra("startDate");
		String endDate = intent.getStringExtra("endDate");
		
		MsgEventHandler.sGetCarTrack(carId,endDate,startDate);//����ʼ����֮ǰ��ѯ
		
		pd = new ProgressDialog(ReplayActivity.this);
		pd.setMessage("��ʷ�켣��ȡ��...");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				handler.sendEmptyMessage(TIME_LIMIT);	
			}
			
		}, TIME_LIMIT);
		moveThread=new MyThread() ;
		
		//mAmap.moveCamera(CameraUpdateFactory.zoomTo(10));
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
		
		
		tbSatelite.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(tbSatelite.isChecked()){
					mAmap.setMapType(AMap.MAP_TYPE_SATELLITE);
				}else{
					mAmap.setMapType(AMap.MAP_TYPE_NORMAL);
				}
				
			}
			
		});
		
		tbPlay.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				moveThread.setControl(1);
			}
			
		});
		tbStop.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				moveThread.setControl(0);
			}
			
		});
		
	}
	

	private  Handler handler = new Handler() {  
	        @Override  
	        public void handleMessage(Message msg) {// handler���յ���Ϣ��ͻ�ִ�д˷���  
	         if(msg.what==NetworkAdapter.MSG_TRACK){
	        	 pd.dismiss();
	        	 timer.cancel();
	        	 Bundle b = msg.getData();
		        	Track[] trackList = (Track[]) b.getParcelableArray("trackList");
		        	initRoadData(trackList);
		        	pd.dismiss();// �ر�ProgressDialog
		        	if (mVirtureRoad.getPoints().size()>1){
		        		
		        		moveThread.start();
		        		Toast.makeText(ReplayActivity.this, "�������Ű�ť�ط���ʷ�켣",Toast.LENGTH_SHORT).show();
		        	}else{
		        		Toast.makeText(ReplayActivity.this, "���ʱ�䴦��ͣ��״̬",Toast.LENGTH_SHORT).show();
		        		return;
		        	}
		    		// �ر�ProgressDialog
		    		
	         }else if (msg.what==NetworkAdapter.MSG_FAIL){
	        	 pd.dismiss();
	        	 timer.cancel();
	        	 Toast.makeText(ReplayActivity.this, "��ȡ���ݴ�������ݿ�������",Toast.LENGTH_SHORT).show();
	         }else if (msg.what==TIME_LIMIT){
	        	 pd.dismiss();
	        	 Toast.makeText(ReplayActivity.this, "�豸�ػ�������״���������ݷ��س�ʱ",Toast.LENGTH_SHORT).show();
	        	 return;
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
			mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(correctCoordinate[0], correctCoordinate[1]), 18));
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
			Toast.makeText(this,"û�й켣��¼", Toast.LENGTH_SHORT).show();
		}
		

	}

	/**
	 * ���ݵ��ȡͼ��ת�ĽǶ�
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
	 * ����������ȡͼ��ת�ĽǶ�
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
	 * ���ݵ��б����ȡ�ؾ�
	 */
	private double getInterception(double slope, LatLng point) {

		double interception = point.latitude - slope * point.longitude;
		return interception;
	}

	/**
	 * ��ȡб��
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
	 * ��б��
	 */
	private double getSlope(LatLng fromPoint, LatLng toPoint) {
		if (toPoint.longitude == fromPoint.longitude) {
			return Double.MAX_VALUE;
		}
		double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
		return slope;

	}

	/**
	 * ����������д
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
	 * ����������д
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	/**
	 * ����x����ÿ���ƶ��ľ���
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
	 * ѭ�������ƶ��߼�
	 */
	
	class MyThread extends Thread{
		
		private int control = 0; // ֻ����Ҫһ��������ѣ��������û��ʵ������  
		private int progress = 0;
		public void setControl(int c){
			control=c;
		}
		
		public void setProgress(int c){
			progress=c;
		}
		public int getProgress(){
			return progress;
		}


		public void run() {
			
			for (; progress < mVirtureRoad.getPoints().size() - 1;) {

				if(control == 1){
						
						LatLng startPoint = mVirtureRoad.getPoints().get(progress);
						LatLng endPoint = mVirtureRoad.getPoints().get(progress + 1);
						mMoveMarker.setPosition(startPoint);
			
						mMoveMarker.setRotateAngle((float) getAngle(startPoint,endPoint));
			
						double slope = getSlope(startPoint, endPoint);
						//�ǲ�������ı�ʾ��������Ϊ����
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
							
							
							try {
								Thread.sleep(TIME_INTERVAL);
							}catch (InterruptedException e) {
								e.printStackTrace();
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						progress++;
				
				}else{
					
				}

				
					
				

			}
		}
		

	}
	//��ͼ�߳�
	
//	public void moveLooper() {
		
		
//	}


	

}
