package com.cloudbean.trackme;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.cloudbean.model.Alarm;
import com.cloudbean.model.Login;
import com.cloudbean.model.Track;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AlarmListActivity extends ListActivity {
	
	private ProgressDialog pd = null;
	private TrackApp ta=null;
	//超时相关控制
	private Timer timer = null;
	private static final int TIME_LIMIT = 8000;
	SimpleAdapter adapter = null;
	List data = new ArrayList<Map<String,?>>();  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_alarm_list);
		
		
	}
	
	  private List<Map<String, Object>> getData(Alarm[] alarmList) {

	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	        for (int ii = 0 ; ii<alarmList.length;ii++){
	        	Map<String, Object> map = new HashMap<String, Object>();
	        	map.put("alarmCarName", ta.currentCar.name);
	        	map.put("alarmType", alarmList[ii].alarmType);
	        	map.put("alarmTime", alarmList[ii].alarmTime);
	        	list.add(map);
	        }

	         

	        return list;

	 }
	  
	  private  Handler handler = new Handler() {  
	        @Override  
	        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
	         
	        	if( msg.what==NetworkAdapter.MSG_ALARM){
	        		 pd.dismiss();
		        	 timer.cancel();
		        	 Bundle b = msg.getData();
		        	 Alarm[] alarmList = (Alarm[]) b.getParcelableArray("alarmlist");
		        	 data= getData(alarmList);
		        	 adapter = new SimpleAdapter(AlarmListActivity.this,data,R.layout.alarm_detail_list,
		 	                new String[]{"alarmCarName","alarmType","alarmTime"},
		 	                new int[]{R.id.alarmCarName,R.id.alarmType,R.id.alarmTime});
		        	 setListAdapter(adapter);
		        	//adapter.notifyDataSetChanged();  
	        	}else if (msg.what==TIME_LIMIT){
	           	 pd.dismiss();
	        	 Toast.makeText(AlarmListActivity.this, "设备关机或网络状况导致数据返回超时",Toast.LENGTH_SHORT).show();
	        	 return;
	         }
	        	  
	      }
	        	
	 }; 
	private static String subDateMinute(String day, int x)//返回的是字符串型的时间，输入的 
	//是String day, int x 
	 {    
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制   
	//引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变 
	//量day格式一致 
	        Date date = null;    
	        try {    
	            date = format.parse(day);    
	        } catch (Exception ex) {    
	            ex.printStackTrace();    
	        }    
	        if (date == null)    
	            return "";    
	        //System.out.println("front:" + format.format(date)); //显示输入的日期   
	        Calendar cal = Calendar.getInstance();    
	        cal.setTime(date);    
	        cal.add(Calendar.MINUTE, -x);// 24小时制    
	        date = cal.getTime();    
	        //System.out.println("after:" + format.format(date));  //显示更新后的日期  
	        cal = null;    
	        return format.format(date);    
	   
	 }   
	protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			 
			 ta = (TrackApp)getApplication();
			 ta.setHandler(handler);
			 Date date= new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			MsgEventHandler.sGetAlarmList(ta.currentCar.id, subDateMinute(format.format(date),20), format.format(date), "");
			
			
			pd = new ProgressDialog(AlarmListActivity.this);
			pd.setMessage("报警信息获取中...");
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
}
