package com.cloudbean.trackme;

import java.util.Timer;
import java.util.TimerTask;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.cloudbean.model.Login;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackerUtil.GpsCorrect;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetCommandActivity extends Activity {
	private Button btSetDef = null;
	private Button btCancelDef = null;
	private Button btDisconCircuit = null;
	private Button btConnCircuit = null;
	private TrackApp ta =null;
	private ProgressDialog pd = null;
	
	//超时相关控制
	private Timer timer = null;
	private static final int TIME_LIMIT = 8000;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_command);
		
		btSetDef = (Button)findViewById(R.id.set_def);
		btCancelDef = (Button)findViewById(R.id.cancel_def);
		btDisconCircuit = (Button)findViewById(R.id.discon_circuit);
		btConnCircuit = (Button)findViewById(R.id.conn_circuit);
		
		
		btSetDef.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				MsgEventHandler.c_sSetDef(ta.currentCar, "01");
				pd = new ProgressDialog(SetCommandActivity.this);
				pd.setMessage("命令发送中...");
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
			
		});
		
		btCancelDef.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MsgEventHandler.c_sSetDef(ta.currentCar, "00");
				pd = new ProgressDialog(SetCommandActivity.this);
				pd.setMessage("命令发送中...");
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
			
		});
		
		
		btConnCircuit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MsgEventHandler.c_sSetCircuit(ta.currentCar, "010202020202");
				pd = new ProgressDialog(SetCommandActivity.this);
				pd.setMessage("命令发送中...");
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
			
		});
		
		
		btDisconCircuit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MsgEventHandler.c_sSetCircuit(ta.currentCar, "000202020202");
				pd = new ProgressDialog(SetCommandActivity.this);
				pd.setMessage("命令发送中...");
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
			
		});
		
	}
	 private  Handler handler = new Handler() {  
	        @Override  
	        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
	         
	        	if( msg.what==CNetworkAdapter.MSG_DEF){
	        		timer.cancel();
	        		 Bundle b = msg.getData();
	        		 String devid = b.getString("devid");
	        		 int i  = devid.indexOf("f");	
	        		 devid = devid.substring(0, i);
	    			 if(devid.equals(ta.currentCar.devId)){
	    				 	pd.dismiss();
	    					String res =  b.getString("res");
	    					if(res.equals("0100")){
	    						Toast.makeText(SetCommandActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
	    					}else if(res.equals("0000")){
	    						Toast.makeText(SetCommandActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
	    					}else{
	    						Toast.makeText(SetCommandActivity.this, "服务器回复错误", Toast.LENGTH_SHORT).show();
	    					}
	    				 
	    			 }
	        	
	        	}else if(msg.what==CNetworkAdapter.MSG_CIRCUIT){
	        		timer.cancel();
	        		 Bundle b = msg.getData();
	        		 String devid = b.getString("devid");
	        		 int i  = devid.indexOf("f");	
	        		 devid = devid.substring(0, i);
	    			 if(devid.equals(ta.currentCar.devId)){
	    				 	pd.dismiss();
	    					String res =  b.getString("res");
	    					if(res.equals("01")){
	    						Toast.makeText(SetCommandActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
	    					}else if(res.equals("00")){
	    						Toast.makeText(SetCommandActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
	    					}else{
	    						Toast.makeText(SetCommandActivity.this, "服务器回复错误", Toast.LENGTH_SHORT).show();
	    					}
	    				 
	    			 }
	        	}else if (msg.what==TIME_LIMIT){
		           	 pd.dismiss();
		        	 Toast.makeText(SetCommandActivity.this, "设备关机或网络状况导致数据返回超时",Toast.LENGTH_SHORT).show();
		        	 return;
		         }
	        }
	        	
	 };
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		 ta = (TrackApp)getApplication();
		 ta.setHandler(handler);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_command, menu);
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
