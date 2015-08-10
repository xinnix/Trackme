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
	// ͨ�����ü��ʱ��;�����Կ����ٶȺ�ͼ���ƶ��ľ���
	private static final int TIME_INTERVAL = 20;
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
		pd.setMessage("��ʷ����ȡ��...");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
		
		//mAmap.moveCamera(CameraUpdateFactory.zoomTo(10));
		
	}
	

	private  Handler handler = new Handler() {  
	        @Override  
	        public void handleMessage(Message msg) {// handler���յ���Ϣ��ͻ�ִ�д˷���  
	         if(msg.what==NetworkAdapter.MSG_SUCCESS){
	        	 Bundle b = msg.getData();
		        	Track[] trackList = (Track[]) b.getParcelableArray("trackList");
		        	initRoadData(trackList);
		    		moveLooper();
		    		pd.dismiss();// �ر�ProgressDialog
	         }else{
	        	 pd.dismiss();// �ر�ProgressDialog
	        	 Toast.makeText(getApplicationContext(), "��ȡ���ݴ�������ݿ�������",Toast.LENGTH_SHORT).show();
	         }
	        	
	        }
	        	
	 };
	private void initRoadData(Track[] tracklist) {
		// 116.504505 39.931057
	//	double centerLatitude = 39.916049;
	//	double centerLontitude = 116.399792;
		if (tracklist.length>0){
			mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tracklist[0].latitude, tracklist[0].longitude), 14));
			int num=tracklist.length;
//			if(num>10)
//			{
//				
//			}
			double centerLatitude = tracklist[0].latitude;
			double centerLontitude = tracklist[0].longitude;
			double deltaAngle = Math.PI / 180 * 5;
			double radius = 0.02;
	    	PolylineOptions polylineOptions = new PolylineOptions();
//			for (double i = 0; i < Math.PI * 2; i = i + deltaAngle) {
//				float latitude = (float) (-Math.cos(i) * radius + centerLatitude);
//				float longtitude = (float) (Math.sin(i) * radius + centerLontitude);
//				polylineOptions.add(new LatLng(latitude, longtitude));
//				if (i > Math.PI) {
//					deltaAngle = Math.PI / 180 * 30;
//				}
//			}
			float latitude = (float) (-Math.cos(0) * radius + centerLatitude);
			float longtitude = (float) (Math.sin(0) * radius + centerLontitude);
			polylineOptions.add(new LatLng(latitude, longtitude));
	    	//new LatLng(socket9.weidu.get(0),socket9.jingdu.get(0))
			for(int i=0;i<num;i++)
			{
				 polylineOptions.add(new LatLng(tracklist[i].latitude, tracklist[i].longitude));
			}
			

			polylineOptions.width(10);
			polylineOptions.color(Color.RED);
			mVirtureRoad = mAmap.addPolyline(polylineOptions);
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.anchor(0.5f, 0.5f);
			markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
			markerOptions.position(polylineOptions.getPoints().get(0));
			mMoveMarker = mAmap.addMarker(markerOptions);
			mMoveMarker.setRotateAngle((float) getAngle(0));
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
		if (slope == Double.MAX_VALUE) {
			return DISTANCE;
		}
		return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
	}

	/**
	 * ѭ�������ƶ��߼�
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
						//�ǲ�������ı�ʾ��������Ϊ����
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
