package com.cloudbean.trackme.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.cloudbean.model.Alarm;
import com.cloudbean.model.Login;
import com.cloudbean.model.Track;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackme.AppManager;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;
import com.cloudbean.trackme.R.menu;
import com.cloudbean.trackme.dialog.IPDialog;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AlarmListActivity extends BaseActivity {
	private ListView lv =null;
	private Button btClearAlarmList = null;
	
	private CheckBox cbSetMute = null;
	
	SimpleAdapter adapter = null;
	List data = new ArrayList<Map<String,?>>();  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	 private List<Map<String, Object>> getData(List alarmList) {

	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	        for (Iterator l =alarmList.iterator();l.hasNext();){
	        	Alarm al = (Alarm) l.next();
	        	Map<String, Object> map = new HashMap<String, Object>();
	        	map.put("alarmCarName", al.termName);
	        	map.put("alarmId", al.termid);
	        	map.put("alarmType", al.alarmType);
	        	map.put("alarmTime", al.alarmTime);
	        	list.add(map);
	        }

	         

	        return list;

	 }
	 
	
	  
//	  private  Handler handler = new Handler() {  
//	        @Override  
//	        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
//	         
//	        	if( msg.what==NetworkAdapter.MSG_ALARM){
//	        		 dismissProgressDialog();
//		        	 timerStop();
//		        	 Bundle b = msg.getData();
//		        	 Alarm[] alarmList = (Alarm[]) b.getParcelableArray("alarmlist");
//		        	 data= getData(alarmList);
//		        	 adapter = new SimpleAdapter(AlarmListActivity.this,data,R.layout.alarm_detail_list,
//		 	                new String[]{"alarmCarName","alarmType","alarmTime"},
//		 	                new int[]{R.id.alarmCarName,R.id.alarmType,R.id.alarmTime});
//		        	 lv.setAdapter(adapter);
//		        	//adapter.notifyDataSetChanged();  
//	        	}else if (msg.what==TIME_OUT){
//	           	 pd.dismiss();
//	        	 Toast.makeText(AlarmListActivity.this, "设备关机或网络状况导致数据返回超时",Toast.LENGTH_SHORT).show();
//	        	 return;
//	         }
//	        	  
//	      }
//	        	
//	 }; 
//	private static String subDateMinute(String day, int x)//返回的是字符串型的时间，输入的 
//	//是String day, int x 
//	 {    
//	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制   
//	//引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变 
//	//量day格式一致 
//	        Date date = null;    
//	        try {    
//	            date = format.parse(day);    
//	        } catch (Exception ex) {    
//	            ex.printStackTrace();    
//	        }    
//	        if (date == null)    
//	            return "";    
//	        //System.out.println("front:" + format.format(date)); //显示输入的日期   
//	        Calendar cal = Calendar.getInstance();    
//	        cal.setTime(date);    
//	        cal.add(Calendar.MINUTE, -x);// 24小时制    
//	        date = cal.getTime();    
//	        //System.out.println("after:" + format.format(date));  //显示更新后的日期  
//	        cal = null;    
//	        return format.format(date);    
//	   
//	 }   
	protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume(); 
			
			
			cbSetMute.setChecked(TrackApp.isMute);
			
			data= getData(TrackApp.alarmList);
	       	 adapter = new SimpleAdapter(AlarmListActivity.this,data,R.layout.alarm_detail_list,
		                new String[]{"alarmId","alarmCarName","alarmType","alarmTime"},
		                new int[]{R.id.alarmId,R.id.alarmCarName,R.id.alarmType,R.id.alarmTime});
	       	 lv.setAdapter(adapter);
//			Date date= new Date();
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////			MsgEventHandler.sGetAlarmList(TrackApp.currentCar.id, subDateMinute(format.format(date),20), format.format(date), "");
////			showProgressDialog("报警信息获取中...");
//			timerStart();
	}

	

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_alarm_list);
		lv = (ListView) findViewById(R.id.alarmList);
		btClearAlarmList = (Button) findViewById(R.id.bt_clearAlarmList);
		cbSetMute = (CheckBox) findViewById(R.id.cb_setmute);
		btClearAlarmList.setOnClickListener(this);
		
		cbSetMute.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton cb, boolean ischecked) {
				// TODO Auto-generated method stub
				
				TrackApp.setMute(ischecked);
				TrackApp.isMute = ischecked;
				
			}
			
		});
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.bt_clearAlarmList:
			TrackApp.alarmList.clear();
			data= getData(TrackApp.alarmList);
			 adapter = new SimpleAdapter(AlarmListActivity.this,data,R.layout.alarm_detail_list,
		                new String[]{"alarmId","alarmCarName","alarmType","alarmTime"},
		                new int[]{R.id.alarmId,R.id.alarmCarName,R.id.alarmType,R.id.alarmTime});
	       	 lv.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			break;
		
	
	
		}
	}

	@Override
	public void handleMsg(Message msg) {
//		// TODO Auto-generated method stub
		if( msg.what==CNetworkAdapter.MSG_ALARM){
			data= getData(TrackApp.alarmList);
			 adapter = new SimpleAdapter(AlarmListActivity.this,data,R.layout.alarm_detail_list,
		                new String[]{"alarmId","alarmCarName","alarmType","alarmTime"},
		                new int[]{R.id.alarmId,R.id.alarmCarName,R.id.alarmType,R.id.alarmTime});
	       	 lv.setAdapter(adapter);
	   		adapter.notifyDataSetChanged();
	    }
	}
}
