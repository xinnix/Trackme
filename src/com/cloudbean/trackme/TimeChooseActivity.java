package com.cloudbean.trackme;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class TimeChooseActivity extends Activity{
	



	private static boolean isShow = true;
	private EditText etDateText = null ;
	
	private EditText etPeriodText = null ;
	//private String initStartDateTime = "2015年7月7日 14:44"; // 初始化开始时间  
	private final String[] items =new String[]{"1小时","2小时"}; 

	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_timechoose);
		
		
		etDateText = (EditText)findViewById(R.id.etDateText);
		
		etPeriodText = (EditText)findViewById(R.id.etPeriodText);
		
		etDateText.setOnTouchListener(new View.OnTouchListener(){

			
			int touchFlag = 0;
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				touchFlag++;
				if(touchFlag==2){
					touchFlag=0;
					DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(  
							TimeChooseActivity.this);  
					dateTimePicKDialog.dateTimePicKDialog(etDateText);  
				}
				
				return false;
			}
	
		});
		
		etPeriodText.setOnTouchListener(new View.OnTouchListener(){

		
			int touchFlag = 0;
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				touchFlag++;
				if(touchFlag==2){
					touchFlag=0;
				AlertDialog.Builder builder = new AlertDialog.Builder(

                        TimeChooseActivity.this);

               builder.setTitle("请选择城市");

                 builder.setItems(items, new DialogInterface.OnClickListener() {


					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						 etPeriodText.setText(items[which]); 
					}
                 
                 });

                 builder.setNegativeButton("取消", new OnClickListener() {

                     @Override

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }

                 });

                builder.show();
				}
				return false;
			}
			

			
			
		});
		
		
	}

}
