package com.cloudbean.trackme.activity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.PolylineOptions;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;
import com.cloudbean.trackme.R.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class OtherCommandActivity extends BaseActivity {
	private Button btSetPhone;
	private Button btGpsReboot;
	private Button btSetGpsTimeInterval;
	private Button btReadGprsTimeInterval;
	private Button btSetGpsHeartBeat;
	private Button btSetSportMode;
	private Button btSleepTime;
	private Button btSetReSms;
	private Button btCancelReSms;
	private final String[] periodItems =new String[]{"10秒","30秒","60秒"}; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	
	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_other_command);
		btSetPhone = (Button) findViewById(R.id.set_phone);
		btGpsReboot = (Button) findViewById(R.id.gps_reboot);
		btSetGpsTimeInterval = (Button) findViewById(R.id.set_gprs_time_interval);
		btSetGpsHeartBeat = (Button) findViewById(R.id.set_gps_heartbeat);
		btSleepTime = (Button) findViewById(R.id.set_sleep_time);
		btSetReSms = (Button) findViewById(R.id.set_re_sms);
		btCancelReSms = (Button) findViewById(R.id.cancel_re_sms);
		btSetSportMode = (Button) findViewById(R.id.set_sportmode);
		btReadGprsTimeInterval = (Button) findViewById(R.id.read_gprs_time_interval);
		btSetPhone.setOnClickListener(this);
		btGpsReboot.setOnClickListener(this);
		btSetGpsTimeInterval.setOnClickListener(this);
		btSetGpsHeartBeat.setOnClickListener(this);
		btSleepTime.setOnClickListener(this);
		btSetReSms.setOnClickListener(this);
		btCancelReSms.setOnClickListener(this);
		btSetSportMode.setOnClickListener(this);
		btReadGprsTimeInterval.setOnClickListener(this);
	}
	
	
	

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.set_phone:
			final EditText etPhone = new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("请输入号码：")
			.setView(etPhone)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					showMessage("设置监听号码命令已发送");
					MsgEventHandler.c_sSetPhone(TrackApp.currentCar, etPhone.getText().toString());
				}
			})
			.setNegativeButton("取消", null)
			.show();
			break;
		case R.id.gps_reboot:
			showMessage("GPS重启命令已发送");
			MsgEventHandler.c_sGPSReboot(TrackApp.currentCar, null);
			break;
		case R.id.set_sportmode:
			showMessage("设置运动模式命令已发送");
			MsgEventHandler.c_sSetSportMode(TrackApp.currentCar, null);
			break;
		case R.id.read_gprs_time_interval:
			showMessage("读取GPRS时间间隔命令已发送");
			MsgEventHandler.c_sReadGprsTimeInterval(TrackApp.currentCar, null);
			break;
		case R.id.set_gprs_time_interval:
			AlertDialog.Builder builder = new AlertDialog.Builder(

                    OtherCommandActivity.this);

			builder.setTitle("请选择时间间隔");

             builder.setItems(periodItems, new DialogInterface.OnClickListener() {


				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					 showMessage("设置定时追踪命令已发送");
					 MsgEventHandler.c_sTraceInterval(TrackApp.currentCar, periodItems[which].substring(0, periodItems[which].length()-1));
				}
             
             });

             builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                 @Override

                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }

             });

            builder.show();
			
			
//			final EditText etTrackInterval = new EditText(this);
//			new AlertDialog.Builder(this)
//			.setTitle("请输入定时追踪时间间隔：")
//			.setView(etTrackInterval)
//			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface arg0, int arg1) {
//					// TODO Auto-generated method stub
//					showMessage("设置定时追踪命令已发送");
//					MsgEventHandler.c_sTraceInterval(TrackApp.currentCar, etTrackInterval.getText().toString());
//				}
//			})
//			.setNegativeButton("取消", null)
//			.show();
//			
			break;
			
		case R.id.set_gps_heartbeat:
			final EditText etGPSHeartBeat = new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("请输入心跳时间间隔（0-65535分钟 ）：")
			.setView(etGPSHeartBeat)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					showMessage("设置GPRS心跳命令已发送");
					MsgEventHandler.c_sGPSHeartBeat(TrackApp.currentCar, etGPSHeartBeat.getText().toString());
				}
			})
			.setNegativeButton("取消", null)
			.show();
			
			break;
		case R.id.set_sleep_time:
			final EditText etSleepTime = new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("请输入休眠时间（1-99分钟 ）：")
			.setView(etSleepTime)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					showMessage("设置休眠模式命令已发送");
					String sleepTime = etSleepTime.getText().toString();
					if(sleepTime.length()<2){
						sleepTime = "0"+sleepTime;
					}
					MsgEventHandler.c_sSavePower(TrackApp.currentCar, sleepTime);
				}
			})
			.setNegativeButton("取消", null)
			.show();
			
			break;
		case R.id.set_re_sms:
			showMessage("开启打电话回复经纬度");
			MsgEventHandler.c_sExpandCommand(TrackApp.currentCar, "01000000010000000001");
			break;
		case R.id.cancel_re_sms:
			showMessage("关闭打电话回复经纬度");
			MsgEventHandler.c_sExpandCommand(TrackApp.currentCar, "00000000010000000001");
			break;
		}
	}

	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
