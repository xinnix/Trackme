package com.cloudbean.trackme.activity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.cloudbean.model.Car;
import com.cloudbean.model.Login;
import com.cloudbean.network.BaseNetworkAdapter;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.HeartBeat;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackme.AppManager;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.dialog.IPDialog;
import com.cloudbean.trackme.server.NetWorkService;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends BaseActivity {
	
	private EditText etUsername = null;
	private EditText etPassword = null;
	
	private Button btLogin = null;
	private Button btExit = null;	
	private Button btSetServer = null;
	
	private CheckBox ckRemPassword = null;
	private int flag = 0;
	
	private Login login=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	private void saveUserInfo(String user,String pass){
		SharedPreferences sp = getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit(); 
		editor.putString("name", user);
		editor.putString("password", pass);
		editor.putBoolean("isSaved", true);
		editor.commit();
	}
	
	private void getUserinfo(){
		SharedPreferences sp = getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
		if(sp.getBoolean("isSaved", false)){
			etUsername.setText(sp.getString("name", ""));
			etPassword.setText(sp.getString("password", ""));
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	

	
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}


	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_login);
		etUsername = (EditText)findViewById(R.id.username);
		etPassword = (EditText)findViewById(R.id.password);
		btLogin = (Button)findViewById(R.id.login);
		btExit = (Button)findViewById(R.id.exit);
		btSetServer = (Button)findViewById(R.id.setServer);
		ckRemPassword = (CheckBox)findViewById(R.id.remPass);
		ckRemPassword.setChecked(true);
		
		getUserinfo();
		
		btLogin.setOnClickListener(this);
		btExit.setOnClickListener(this);
		btSetServer.setOnClickListener(this);
		
		
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.login:
//				bindService(new Intent(MainActivity.this, NetWorkService.class),conn, Context.BIND_AUTO_CREATE);
				
				connNetwork();
				
				showProgressDialog("登录中");
				timerStart();
				break;
			case R.id.exit:
				AppManager.getAppManager().finishAllActivity();
				break;
			case R.id.setServer:
				IPDialog ipd = new IPDialog(this);
				ipd.show();
				break;
		}
		
	}

	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		
		if(msg.what==NetworkAdapter.MSG_LOGIN){
    		Bundle b = msg.getData();
    		login = (Login) b.get("login");
        	
        	if(login.isLogin==Login.LOGIN_SUCCESS){
        		TrackApp.login = login;
        		dismissProgressDialog();
        		
        		if(ckRemPassword.isChecked()){
        			saveUserInfo(etUsername.getText().toString(),etPassword.getText().toString());
        		}
        		
//        		TrackApp.hb.start();

        		MsgEventHandler.sGetCarInfo(login.userid,"");
        		
        		
        		TrackApp.curPassword = etPassword.getText().toString();
        		TrackApp.curUsername = etUsername.getText().toString();
        		
				
        	}else{
        		dismissProgressDialog();
        		timerStop();
            	showMessage("登录失败");
            	return;
        	}
        	
    		//Toast.makeText(MainActivity.this, "获取数据错误或数据库无数据",Toast.LENGTH_SHORT).show();
    	}else if(msg.what == NetworkAdapter.MSG_CARINFO){
    		MsgEventHandler.sGetCarGroup(login.userid,"");
		
    	}else if(msg.what == BaseNetworkAdapter.NETWORK_CONNECTED){
    		
    		MsgEventHandler.sLogin(etUsername.getText().toString(), etPassword.getText().toString());
			MsgEventHandler.c_sLogin(etUsername.getText().toString(), etPassword.getText().toString());
    	}else if(msg.what == NetworkAdapter.MSG_CARGROUPINFO){
    		timerStop();
    		dismissProgressDialog();
    		showMessage("登录成功");
    		Date date= new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String carListId = getCarListIdString(TrackApp.carList);
			MsgEventHandler.sGetAlarmList(carListId,subDateMinute(format.format(date),5), format.format(date), "");
    		TrackApp.isLogin = true;
    		MsgEventHandler.c_sGetAllLastPosition();
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, MenuActivity.class);
			intent.putExtra("userId",TrackApp.login.userid);
			startActivity(intent);
			finish();
    	}else if (msg.what==TIME_OUT){
    		TrackApp.na.reconnThread.interrupt();
    		TrackApp.cna.reconnThread.interrupt();
    		dismissProgressDialog();
       	 	showMessage("设备关机或网络状况导致数据返回超时");
       	 	return;
		}
		
		
	}
	
	
	private String getCarListIdString(Car[] carList){
		String res = "";
		if(carList!=null){
			for(int jj=0 ; jj<carList.length; jj++){
				res = res+carList[jj].id+",";
			}
		}
		
		return res.substring(0,res.length()-1);
	}
	
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
	
	public  void connNetwork(){
		String[] d = decodeAddr(TrackApp.dServerAddr);
		String[] c = decodeAddr(TrackApp.cServerAddr);
		
		String dip = d[0];
		int dport = Integer.parseInt(d[1]);
		
		String cip = c[0];
		int cport = Integer.parseInt(c[1]);
		Context context = getApplicationContext();
		TrackApp.na = new NetworkAdapter(dip,dport);
		TrackApp.cna = new CNetworkAdapter(cip,cport,context);
		TrackApp.hb = new HeartBeat();
		MsgEventHandler.config(TrackApp.na, TrackApp.cna);
	}
	public  String[] decodeAddr(String addr){
		return addr.split(":");
		
	}
	
}
