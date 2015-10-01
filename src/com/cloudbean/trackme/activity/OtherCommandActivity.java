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
	private Button btSetGpsHeartBeat;
	private Button btSleepTime;
	private Button btSetReSms;
	private Button btCancelReSms;

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
		btSetGpsTimeInterval = (Button) findViewById(R.id.set_gps_time_interval);
		btSetGpsHeartBeat = (Button) findViewById(R.id.set_gps_heartbeat);
		btSleepTime = (Button) findViewById(R.id.set_sleep_time);
		btSetReSms = (Button) findViewById(R.id.set_re_sms);
		btCancelReSms = (Button) findViewById(R.id.cancel_re_sms);
		btSetPhone.setOnClickListener(this);
		btGpsReboot.setOnClickListener(this);
		btSetGpsTimeInterval.setOnClickListener(this);
		btSetGpsHeartBeat.setOnClickListener(this);
		btSleepTime.setOnClickListener(this);
		btSetReSms.setOnClickListener(this);
		btCancelReSms.setOnClickListener(this);
	}
	
	
	

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.set_phone:
			final EditText etPhone = new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("��������룺")
			.setView(etPhone)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					showMessage("���ü������������ѷ���");
					MsgEventHandler.c_sSetPhone(TrackApp.currentCar, etPhone.getText().toString());
				}
			})
			.setNegativeButton("ȡ��", null)
			.show();
			break;
		case R.id.gps_reboot:
			showMessage("GPS���������ѷ���");
			MsgEventHandler.c_sGPSReboot(TrackApp.currentCar, null);
			break;
		case R.id.set_gps_time_interval:
			final EditText etTrackInterval = new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("�����붨ʱ׷��ʱ��������λΪ10�� ����")
			.setView(etTrackInterval)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					showMessage("���ö�ʱ׷�������ѷ���");
					MsgEventHandler.c_sGPSHeartBeat(TrackApp.currentCar, etTrackInterval.getText().toString());
				}
			})
			.setNegativeButton("ȡ��", null)
			.show();
			
			break;
			
		case R.id.set_gps_heartbeat:
			final EditText etGPSHeartBeat = new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("����������ʱ������0-65535���� ����")
			.setView(etGPSHeartBeat)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					showMessage("����GPRS���������ѷ���");
					MsgEventHandler.c_sGPSHeartBeat(TrackApp.currentCar, etGPSHeartBeat.getText().toString());
				}
			})
			.setNegativeButton("ȡ��", null)
			.show();
			
			break;
		case R.id.set_sleep_time:
			final EditText etSleepTime = new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("����������ʱ�䣨���� ����")
			.setView(etSleepTime)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					showMessage("��������ģʽ�����ѷ���");
					MsgEventHandler.c_sSavePower(TrackApp.currentCar, etSleepTime.getText().toString());
				}
			})
			.setNegativeButton("ȡ��", null)
			.show();
			
			break;
		case R.id.set_re_sms:
			showMessage("������绰�ظ���γ��");
			MsgEventHandler.c_sExpandCommand(TrackApp.currentCar, "01000000010000000001");
			break;
		case R.id.cancel_re_sms:
			showMessage("�رմ�绰�ظ���γ��");
			MsgEventHandler.c_sExpandCommand(TrackApp.currentCar, "00000000010000000001");
			break;
		}
	}

	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
