package com.cloudbean.trackme.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.trackerUtil.DateTimePickDialogUtil;
import com.cloudbean.trackerUtil.DateTimeUtil;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class TimeChooseActivity extends BaseActivity{

	private static boolean isShow = true;
	private EditText etDateText = null ;
	private EditText etPeriodText = null ;
	private Button btReply = null;
	
	
	
	private int carId = 0;
	//private String initStartDateTime = "2015年7月7日 14:44"; // 初始化开始时间  
	private final String[] periodItems =new String[]{"1小时","2小时","3小时","4小时","5小时","6小时","7小时","8小时",
			"9小时","10小时","11小时","12小时","13小时","14小时","15小时","16小时",
			"17小时","18小时","19小时","20小时","21小时","22小时","23小时","24小时"}; 

	

	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		
		Intent intent = this.getIntent();
		Bundle b  = intent.getExtras();
		carId = b.getInt("carid");
		setFinishOnTouchOutside(false);
		
		
		
		etDateText.setOnClickListener(new View.OnClickListener(){
//			int touchFlag = 0;
//			@Override
//			public boolean onTouch(View arg0, MotionEvent arg1) {
//				// TODO Auto-generated method stub
//				touchFlag++;
//				if(touchFlag==2){
//					touchFlag=0;
//					DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(  
//							TimeChooseActivity.this);  
//					dateTimePicKDialog.dateTimePicKDialog(etDateText);  
//				}
//				
//				return false;
//			}
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(  
						TimeChooseActivity.this);  
				dateTimePicKDialog.dateTimePicKDialog(etDateText);
			}
	
		});
		
		etPeriodText.setOnClickListener(new View.OnClickListener(){
//			int touchFlag = 0;
			
//			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
//				touchFlag++;
//				if(touchFlag==2){
//					touchFlag=0;
//					DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(  
//							TimeChooseActivity.this);  
//					dateTimePicKDialog.dateTimePicKDialog(etPeriodText);  
//				}
//				
//				return false;
//			}
//	

		
//			int touchFlag = 0;
//			@Override
//			public boolean onTouch(View arg0, MotionEvent arg1) {
//				// TODO Auto-generated method stub
//				touchFlag++;
//				if(touchFlag==2){
//					touchFlag=0;
//				AlertDialog.Builder builder = new AlertDialog.Builder(
//
//                        TimeChooseActivity.this);
//
//               builder.setTitle("请选择轨迹参数");
//
//                 builder.setItems(periodItems, new DialogInterface.OnClickListener() {
//
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						 etPeriodText.setText(periodItems[which]); 
//					}
//                 
//                 });
//
//                 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//                     @Override
//
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//                    }
//
//                 });
//
//                builder.show();
//				}
//				return false;
//			}
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(

                        TimeChooseActivity.this);

               builder.setTitle("请选择轨迹参数");

               builder.setItems(periodItems, new DialogInterface.OnClickListener() {


					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						 etPeriodText.setText(periodItems[which]); 
					}
                 
                 });

                 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                     @Override

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }

                 });

                builder.show();
			}

			
		});
		
		
	}


	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_timechoose);		
		etDateText = (EditText)findViewById(R.id.etDateText);		
		etPeriodText = (EditText)findViewById(R.id.etPeriodText);
		btReply = (Button)findViewById(R.id.btReply);
		btReply.setOnClickListener(this);
		Date d= new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		etDateText.setText(format.format(d));
		etPeriodText.setText("1小时");
	}


	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btReply:
			String startDate = etDateText.getText().toString();// TODO Auto-generated method stub
			String period = etPeriodText.getText().toString();
			
			String endDate=DateTimeUtil.subDateHour(startDate,Integer.parseInt(period.substring(0, period.length()-2)));
			
//			int carId = TimeChooseActivity.this.carId;
//			startDate = DateTimeUtil.string2TimezoneDefault(startDate, "GMT");
//			endDate = DateTimeUtil.string2TimezoneDefault(endDate, "GMT");
			Intent intent = new Intent();
			intent.setClass(TimeChooseActivity.this, ReplayActivity.class);
			intent.putExtra("startDate", startDate);
			intent.putExtra("endDate", endDate);
			intent.putExtra("carId", Integer.parseInt(TrackApp.currentCar.id));
			intent.putExtra("isTimeSelected", "ok");
			startActivity(intent);
			finish();
			break;
		
		}
	}


	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		
	}

}
