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
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;
import com.cloudbean.trackme.R.menu;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AlarmListActivity extends BaseActivity {
	private ListView lv =null;
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
	        	map.put("alarmCarName", al.termid);
	        	map.put("alarmType", al.alarmType);
	        	map.put("alarmTime", al.alarmTime);
	        	list.add(map);
	        }

	         

	        return list;

	 }
	  
//	  private  Handler handler = new Handler() {  
//	        @Override  
//	        public void handleMessage(Message msg) {// handler���յ���Ϣ��ͻ�ִ�д˷���  
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
//	        	 Toast.makeText(AlarmListActivity.this, "�豸�ػ�������״���������ݷ��س�ʱ",Toast.LENGTH_SHORT).show();
//	        	 return;
//	         }
//	        	  
//	      }
//	        	
//	 }; 
//	private static String subDateMinute(String day, int x)//���ص����ַ����͵�ʱ�䣬����� 
//	//��String day, int x 
//	 {    
//	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24Сʱ��   
//	//�����������ʽҲ������ HH:mm:ss����HH:mm�ȵȣ�������ģ�����������������ʱ��Ҫ������ı� 
//	//��day��ʽһ�� 
//	        Date date = null;    
//	        try {    
//	            date = format.parse(day);    
//	        } catch (Exception ex) {    
//	            ex.printStackTrace();    
//	        }    
//	        if (date == null)    
//	            return "";    
//	        //System.out.println("front:" + format.format(date)); //��ʾ���������   
//	        Calendar cal = Calendar.getInstance();    
//	        cal.setTime(date);    
//	        cal.add(Calendar.MINUTE, -x);// 24Сʱ��    
//	        date = cal.getTime();    
//	        //System.out.println("after:" + format.format(date));  //��ʾ���º������  
//	        cal = null;    
//	        return format.format(date);    
//	   
//	 }   
	protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume(); 
			
			
				data= getData(TrackApp.alarmList);
	       	 adapter = new SimpleAdapter(AlarmListActivity.this,data,R.layout.alarm_detail_list,
		                new String[]{"alarmCarName","alarmType","alarmTime"},
		                new int[]{R.id.alarmCarName,R.id.alarmType,R.id.alarmTime});
	       	 lv.setAdapter(adapter);
//			Date date= new Date();
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////			MsgEventHandler.sGetAlarmList(TrackApp.currentCar.id, subDateMinute(format.format(date),20), format.format(date), "");
////			showProgressDialog("������Ϣ��ȡ��...");
//			timerStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm_list, menu);
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

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_alarm_list);
		lv = (ListView) findViewById(R.id.alarmList);
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMsg(Message msg) {
//		// TODO Auto-generated method stub
//		if( msg.what==NetworkAdapter.MSG_ALARM){
//	   		 dismissProgressDialog();
//	       	 timerStop();
//	       	 Bundle b = msg.getData();
//	       	 Alarm[] alarmList = (Alarm[]) b.getParcelableArray("alarmlist");
//	       	 data= getData(alarmList);
//	       	 adapter = new SimpleAdapter(AlarmListActivity.this,data,R.layout.alarm_detail_list,
//		                new String[]{"alarmCarName","alarmType","alarmTime"},
//		                new int[]{R.id.alarmCarName,R.id.alarmType,R.id.alarmTime});
//	       	 lv.setAdapter(adapter);
//       	//adapter.notifyDataSetChanged();  
//	   	}else if (msg.what==TIME_OUT){
//	   		dismissProgressDialog();
//		   	showMessage("�豸�ػ�������״���������ݷ��س�ʱ");
//		   	return;
//	    }
	}
}
