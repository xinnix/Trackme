package com.cloudbean.trackme;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.cloudbean.model.Car;
import com.cloudbean.model.Login;
import com.cloudbean.model.Track;
import com.cloudbean.trackerUtil.MsgEventHandler;

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
	private static final int TIME_INTERVAL = 80;
	private static final double DISTANCE = 0.0001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply);
		mMapView = (MapView) findViewById(R.id.replymap);

		mMapView.onCreate(savedInstanceState);
		mAmap = mMapView.getMap();
		//mAmap.setPointToCenter((int) (22.90923 * 1E6), (int) (116.397428 * 1E6));
		
		MsgEventHandler.sGetCarTrack(3,"2015-07-05 10:11:11","2015-07-05 10:17:00");
		
		pd = new ProgressDialog(ReplyActivity.this);
		pd.setMessage("��ʷ����ȡ��...");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		//mAmap.moveCamera(CameraUpdateFactory.zoomTo(10));
		
	}
	
	 private  Handler handler = new Handler() {  
	        @Override  
	        public void handleMessage(Message msg) {// handler���յ���Ϣ��ͻ�ִ�д˷���  
	         
	        	Bundle b = msg.getData();
	        	Track[] trackList = (Track[]) b.getParcelableArray("trackList");
	        	initRoadData(trackList);
	    		moveLooper();
	    		pd.dismiss();// �ر�ProgressDialog
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
			
//			 polylineOptions.add(new LatLng(socket9.weidu.get(0), socket9.jingdu.get(0)));
//			 polylineOptions.add(new LatLng(socket9.weidu.get(1), socket9.jingdu.get(1)));
//			 polylineOptions.add(new LatLng(socket9.weidu.get(2), socket9.jingdu.get(2)));
//			 polylineOptions.add(new LatLng(39.919324, 116.379367));
//			 polylineOptions.add(new LatLng(39.951704, 116.356708));
//			
//			
//			
//			
//			 polylineOptions.add(new LatLng(39.970914, 116.408206));
//			
//			 polylineOptions.add(new LatLng(40.005899, 116.467601));
//			 polylineOptions.add(new LatLng(39.954368, 116.478038));
			polylineOptions.width(10);
			polylineOptions.color(Color.RED);
			mVirtureRoad = mAmap.addPolyline(polylineOptions);
			MarkerOptions markerOptions = new MarkerOptions();
			//markerOptions.setFlat(true);
			markerOptions.anchor(0.5f, 0.5f);
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.marker));
			markerOptions.position(polylineOptions.getPoints().get(0));
			mMoveMarker = mAmap.addMarker(markerOptions);
			mMoveMarker.setRotateAngle((float) getAngle(0));
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
						mMoveMarker
						.setPosition(startPoint);

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


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reply, menu);
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
}
