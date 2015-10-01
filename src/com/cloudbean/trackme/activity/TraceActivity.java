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
	private String addressName = "�ȴ���ַ����";
	private GeocodeSearch geocoderSearch = null;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	
	private RouteSearch routeSearch;
	private WalkRouteResult walkRouteResult;
	private DriveRouteResult driveRouteResult;// �ݳ�ģʽ��ѯ���
	
	
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
		mapView.onCreate(savedInstanceState);// ����Ҫд
//	    geocoderSearch = new GeocodeSearch(this);
//		geocoderSearch.setOnGeocodeSearchListener(this);
		
	    
	}

	protected void onResume() {
		super.onResume();
		mapView.onResume();
		initPosition();
//		if(TrackApp.currentCar.lastState==null){
//			MsgEventHandler.c_sGetCarPosition(TrackApp.currentCar);
//			showMessage("��λ�����ѷ���");
//		}
	}
	
	/**
	 * ����������д
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	
	
//    private  Handler handler = new Handler() {  
//        @Override  
//        public void handleMessage(Message msg) {// handler���յ���Ϣ��ͻ�ִ�д˷���  
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
////					 RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);// ��һ��������ʾһ��Latlng���ڶ�������ʾ��Χ�����ף�������������ʾ�ǻ�ϵ����ϵ����GPSԭ������ϵ
////					 geocoderSearch.getFromLocationAsyn(query);// ����ͬ��������������
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
//					 mMoveMarker.setTitle("�豸��Ϣ");
//					 mMoveMarker.setSnippet(
//						"�豸���ƣ�"+TrackApp.currentCar.name+"\n"+
//						"���꣺"+lat+"%"+lon+"\n"+
//						"�ٶȣ�"+speed.substring(0,4)+"km/h"+"\n"+
//						"���룺"+distant+"km"+"\n"+
//						"����ʱ�䣺"+date+"\n"+
//						"��ѹ��"+voltage+"\n"+
//						"�ź�ǿ��"+gsmStrength);
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
//        	 Toast.makeText(TraceActivity.this, "��ȡ���ݴ�������ݿ�������",Toast.LENGTH_SHORT).show();
//         }else if (msg.what==TIME_OUT){
//        	 pd.dismiss();
//        	 Toast.makeText(TraceActivity.this, "�豸�ػ�������״���������ݷ��س�ʱ",Toast.LENGTH_SHORT).show();
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
						+ "����";
//				showMessage(addressName);
				TrackApp.currentCar.curAddress = addressName;
				
				
				handler.sendEmptyMessage(ADDRESS_COMPLETE);
			} else {
				Toast.makeText(TraceActivity.this, "�޵�ַ���ݷ���",Toast.LENGTH_SHORT).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(TraceActivity.this, "�������",Toast.LENGTH_SHORT).show();;
		} else if (rCode == 32) {
			Toast.makeText(TraceActivity.this, "��Կ����",Toast.LENGTH_SHORT).show();;
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
	
		
		//��λ������
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// ����С�����ͼ��
//		myLocationStyle.strokeColor(Color.BLACK);// ����Բ�εı߿���ɫ
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// ����Բ�ε������ɫ
		// myLocationStyle.anchor(int,int)//����С�����ê��
		myLocationStyle.strokeWidth(0.1f);// ����Բ�εı߿��ϸ
		//aMap.getUiSettings().setMyLocationButtonEnabled(true);
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setMyLocationRotateAngle(180);
		aMap.setLocationSource(this);// ���ö�λ����
		
		
		//����ת����ʼ��
		geocoderSearch = new GeocodeSearch(this); 
		geocoderSearch.setOnGeocodeSearchListener(this);
		
		//·���滮
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
			
			showMessage("��ȡ�豸����λ��");
			if(TrackApp.currentCar.curState!=null){
				initPosition();
			}
			
			
			break;
		case R.id.bt_my_position:
			showProgressDialog("·���滮�У����Ժ�");
			aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
			//���ö�λ������Ϊ��λģʽ �������ɶ�λ��������ͼ������������ת���� 
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
//			handler.postDelayed(t, 12000);// ���ó���12�뻹û�ж�λ����ֹͣ��λ
//			
//			trackFlag = (trackFlag==0)?1:0;
//			if(trackFlag==0){
			polylineOptions = new PolylineOptions();
			polylineOptions.width(8);
			polylineOptions.color(Color.RED);
			showMessage("��ʼ����");
//			}else{
//				
//				poly.setVisible(false); 
//				poly.remove();
//				showMessage("ȡ������");
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
				accState = ByteHexUtil.getBooleanArray(TrackApp.currentCar.curState.portState[1])[1]?"��":"��";
			}else if(TrackApp.currentCar.devtype.equals("VT310")){
				temperature  =  TrackApp.currentCar.curState.temperature;
				accState = ByteHexUtil.getBooleanArray(TrackApp.currentCar.curState.portState[0])[3]?"��":"��";
			}else{
				temperature  =  "0";
				accState= "��״̬";
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
			mMoveMarker.setTitle("�豸��Ϣ");
			mMoveMarker.setSnippet(
					"�豸���ƣ�"+TrackApp.currentCar.name+"\n"+
					"���꣺γ��"+formatter.format(lat)+"����"+formatter.format(lon)+"\n"+
					"�ٶȣ�"+speed+"km/h"+"\n"+
					"��̣�"+distant+"km"+"\n"+
					"����ʱ�䣺"+date+"\n"+
					"�¶ȣ�"+temperature+"��\n"+
					"��ѹ��"+voltage+"V\n"+
					"ACC״̬��"+accState+"\n"+
					"�ź�ǿ��:"+gsmStrength+"\n"+
					"��ַ��"+formatAddress(addressName));
			mMoveMarker.showInfoWindow();
			LatLonPoint latLonPoint =new LatLonPoint(correctCoordinate[0], correctCoordinate[1]);
			getAddress(latLonPoint);
		}else{
			MsgEventHandler.c_sGetCarPosition(TrackApp.currentCar);
			showMessage("��λ�����ѷ���");
			
		}
	}
	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// ��һ��������ʾһ��Latlng���ڶ�������ʾ��Χ�����ף�������������ʾ�ǻ�ϵ����ϵ����GPSԭ������ϵ
		geocoderSearch.getFromLocationAsyn(query);// ����ͬ��������������
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
////					 RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);// ��һ��������ʾһ��Latlng���ڶ�������ʾ��Χ�����ף�������������ʾ�ǻ�ϵ����ϵ����GPSԭ������ϵ
////					 geocoderSearch.getFromLocationAsyn(query);// ����ͬ��������������
//					 
//					
//						
//					
//				 	
//					mMoveMarker.setPosition(new LatLng(correctCoordinate[0], correctCoordinate[1]));
//					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(correctCoordinate[0], correctCoordinate[1]), 14)); 
//					mMoveMarker.setTitle("�豸��Ϣ");
//					mMoveMarker.setSnippet(
//						"�豸���ƣ�"+TrackApp.currentCar.name+"\n"+
//						"���꣺γ��"+Double.toString(lat).substring(0, 6)+"����"+Double.toString(lon).substring(0, 6)+"\n"+
//						"�ٶȣ�"+speed.substring(0,4)+"km/h"+"\n"+
//						"��̣�"+distant+"km"+"\n"+
//						"����ʱ�䣺"+date+"\n"+
//						"��ѹ��"+voltage+"\n"+
//						"�ź�ǿ��"+gsmStrength);
//					
//					timerStop();
//					dismissProgressDialog();
			 }
					
         }else if (msg.what==ADDRESS_COMPLETE){
        	 DecimalFormat formatter = new DecimalFormat("##0.000000");
        	 mMoveMarker.setSnippet(
 					"�豸���ƣ�"+TrackApp.currentCar.name+"\n"+
 					"���꣺γ��"+formatter.format(lat)+"����"+formatter.format(lon)+"\n"+
 					"�ٶȣ�"+speed+"km/h"+"\n"+
 					"��̣�"+distant+"km"+"\n"+
 					"����ʱ�䣺"+date+"\n"+
 					"�¶ȣ�"+temperature+"��\n"+
 					"��ѹ��"+voltage+"V\n"+
 					"ACC״̬��"+accState+"\n"+
 					"�ź�ǿ��:"+gsmStrength+"\n"+
 					"��ַ��"+addressName);
 			mMoveMarker.showInfoWindow();
         }
         else if (msg.what==NetworkAdapter.MSG_FAIL){
        	timerStop();
			dismissProgressDialog();
        	showMessage("��ȡ���ݴ�������ݿ�������");
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
			 * 1.0.2�汾��������������true��ʾ��϶�λ�а���gps��λ��false��ʾ�����綨λ��Ĭ����true Location
			 * API��λ����GPS�������϶�λ��ʽ
			 * ����һ�������Ƕ�λprovider���ڶ�������ʱ�������2000���룬������������������λ���ף����ĸ������Ƕ�λ������
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
//			//mListener.onLocationChanged(aLocation);// ��ʾϵͳС����
//			LatLonPoint startpoint = new LatLonPoint(aLocation.getLatitude(), aLocation.getLongitude());// ��λ�״�Сͼ��
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
			LatLonPoint startpoint = new LatLonPoint(aLocation.getLatitude(), aLocation.getLongitude());// ��λ�״�Сͼ��
			searchRouteResult(startpoint,endpoint);
		
		}
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
	 * ��ʼ����·���滮����
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		LatLonPoint l = new LatLonPoint(22.601888190152657 ,114.05940618447902);
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				l, endPoint);
		
		DriveRouteQuery query = new DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault,
				null, null, "");// ��һ��������ʾ·���滮�������յ㣬�ڶ���������ʾ�ݳ�ģʽ��������������ʾ;���㣬���ĸ�������ʾ�������򣬵����������ʾ���õ�·
		routeSearch.calculateDriveRouteAsyn(query);// �첽·���滮�ݳ�ģʽ��ѯ
		
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
				aMap.clear();// �����ͼ�ϵ����и�����
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						this, aMap, drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				showMessage("û��·�����");
			}
		} else if (rCode == 27) {
			showMessage("�������");
		} else if (rCode == 32) {
			showMessage("��Կ����");
		} else {
			showMessage("��������");
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
//	 * ѭ�������ƶ��߼�
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
//							//�ǲ�������ı�ʾ��������Ϊ����
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
