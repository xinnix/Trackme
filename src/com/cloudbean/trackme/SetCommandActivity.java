package com.cloudbean.trackme;

import java.lang.reflect.Field;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.cloudbean.model.Alarm;
import com.cloudbean.model.Login;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackerUtil.GpsCorrect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SetCommandActivity extends BaseActivity {
	private Button btSetDef = null;
	private Button btCancelDef = null;
	private Button btDisconCircuit = null;
	private Button btConnCircuit = null;
	private AlertDialog alertDialog = null;
	private ListView lvLog = null;
	
	private String curCommand = null;
	
	SimpleAdapter adapter = null;
	List<Map<String, Object>> data = new ArrayList<Map<String, Object>>(); 
	
	private void getData(String res) {
		  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        	Map<String, Object> map = new HashMap<String, Object>();
	        	map.put("commandName", curCommand);
	        	map.put("commandResult", res);
	        	map.put("commandTime", format.format(new Date()));
	        	data.add(map);
	        	
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new SimpleAdapter(SetCommandActivity.this,data,R.layout.commandlog_list,
	                new String[]{"commandName","commandResult","commandTime"},
	                new int[]{R.id.commandName,R.id.commandResult,R.id.commandTime});
    	lvLog.setAdapter(adapter);
		showWaiterAuthorizationDialog();
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
	
	
	  //显示对话框
	    public void showWaiterAuthorizationDialog() {
	    	
	    	//LayoutInflater是用来找layout文件夹下的xml布局文件，并且实例化
			LayoutInflater factory = LayoutInflater.from(SetCommandActivity.this);
			//把activity_login中的控件定义在View中
			final View textEntryView = factory.inflate(R.layout.password_dialog, null);
	         
	        //将LoginActivity中的控件显示在对话框中
			alertDialog = new AlertDialog.Builder(SetCommandActivity.this)
			//点击其他地方不消失
			.setCancelable(false)
			//对话框的标题
	       .setTitle("验证")
	       //设定显示的View
	       .setView(textEntryView)
	       //对话框中的“登陆”按钮的点击事件
	       .setPositiveButton("验证", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int whichButton) { 
	        	   
	  			//获取用户输入的“用户名”，“密码”
	        	//注意：textEntryView.findViewById很重要，因为上面factory.inflate(R.layout.activity_login, null)将页面布局赋值给了textEntryView了
//	        	final EditText etUserName = (EditText)textEntryView.findViewById(R.id.etuserName);
	            final EditText etPassword = (EditText)textEntryView.findViewById(R.id.etPWD);
	            
	          //将页面输入框中获得的“用户名”，“密码”转为字符串
//	   	        String userName = etUserName.getText().toString().trim();
	   	    	String password = etPassword.getText().toString().trim();
	   	    	
	   	    	//现在为止已经获得了字符型的用户名和密码了，接下来就是根据自己的需求来编写代码了
	   	    	//这里做一个简单的测试，假定输入的用户名和密码都是1则进入其他操作页面（OperationActivity）
	   	    	preventDismissDialog();
	   	    	if( password.equals(TrackApp.curPassword)){
	   	    		
	   	    		dismissDialog();
	   	    	}else{
	   	    		
	   	    		Toast.makeText(SetCommandActivity.this, "密码或用户名错误", Toast.LENGTH_SHORT).show();
	   	    	}
	           }
	       })
	       //对话框的“退出”单击事件
	       .setNegativeButton("退出", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int whichButton) {
	        	   SetCommandActivity.this.finish();
	           }
	       })
	       
	        //对话框的创建、显示
			.create();
			alertDialog.show();
		}



    /**
     * 关闭对话框
     */
    private void dismissDialog() {
        try {
            Field field = alertDialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(alertDialog, true);
        } catch (Exception e) {
        }
        alertDialog.dismiss();
    }

    /**
     * 通过反射 阻止关闭对话框
     */
    private void preventDismissDialog() {
        try {
            Field field = alertDialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(alertDialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_set_command);
		btSetDef = (Button)findViewById(R.id.set_def);
		btCancelDef = (Button)findViewById(R.id.cancel_def);
		btDisconCircuit = (Button)findViewById(R.id.discon_circuit);
		btConnCircuit = (Button)findViewById(R.id.conn_circuit);
		lvLog = (ListView)findViewById(R.id.loglist);
		btSetDef.setOnClickListener(this);
		btCancelDef.setOnClickListener(this);
		btConnCircuit.setOnClickListener(this);
		btDisconCircuit.setOnClickListener(this);
	}


	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.set_def:
			MsgEventHandler.c_sSetDef(TrackApp.currentCar, "01");
			curCommand = btSetDef.getText().toString();
			showProgressDialog("发送中...");
			timerStart();
			break;
		case R.id.cancel_def:
			MsgEventHandler.c_sSetDef(TrackApp.currentCar, "00");
			curCommand = btCancelDef.getText().toString();
			showProgressDialog("发送中...");
			timerStart();
			break;
		case R.id.discon_circuit:
			MsgEventHandler.c_sSetCircuit(TrackApp.currentCar, "0002020202");
			curCommand = btConnCircuit.getText().toString();
			showProgressDialog("发送中...");
			timerStart();
			break;
		case R.id.conn_circuit:
			MsgEventHandler.c_sSetCircuit(TrackApp.currentCar, "0102020202");
			curCommand = btDisconCircuit.getText().toString();
			showProgressDialog("发送中...");
			timerStart();
			break;
	}
		
	}


	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		// handler接收到消息后就会执行此方法  
        
    	if( msg.what==CNetworkAdapter.MSG_DEF){
    		timerStop();
    		 Bundle b = msg.getData();
    		 String devid = b.getString("devid");
    		 int i  = devid.indexOf("f");	
    		 devid = devid.substring(0, i);
			 if(devid.equals(TrackApp.currentCar.devId)){
				 	dismissProgressDialog();
					String res =  b.getString("res");
					if(res.equals("0100")){
						showMessage("操作成功");
						getData("操作成功");
					}else if(res.equals("0000")){
						showMessage("操作失败");
						getData("操作失败");
					}else{
						showMessage("服务器回复错误");
					}
					adapter.notifyDataSetChanged();
			 }
    	
    	}else if(msg.what==CNetworkAdapter.MSG_CIRCUIT){
    		timerStop();
    		 Bundle b = msg.getData();
    		 String devid = b.getString("devid");
    		 int i  = devid.indexOf("f");	
    		 devid = devid.substring(0, i);
			 if(devid.equals(TrackApp.currentCar.devId)){
				 	pd.dismiss();
					String res =  b.getString("res");
					if(res.equals("01")){
						showMessage("操作成功");
						getData("操作成功");
					}else if(res.equals("00")){
						showMessage("操作失败");
						getData("操作失败");
					}else{
						showMessage("服务器回复错误");
					}
					adapter.notifyDataSetChanged();
			 }
    	}else if (msg.what==TIME_OUT){
    		dismissProgressDialog();
           	 showMessage("设备关机或网络状况导致数据返回超时");
        	 return;
         }
    
		
	}

	

	
}
