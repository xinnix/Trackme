package com.cloudbean.trackme;

import java.util.Timer;
import java.util.TimerTask;

import com.cloudbean.model.Login;
import com.cloudbean.network.HeartBeat;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackme.TrackApp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private EditText etUsername = null;
	private EditText etPassword = null;
	
	private Button btLogin = null;
	private Button btExit = null;
	private ProgressDialog pd = null;
	private TrackApp ta=null;
	//超时相关控制
	private Timer timer = null;
	private static final int TIME_LIMIT = 8000;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etUsername = (EditText)findViewById(R.id.username);
		etPassword = (EditText)findViewById(R.id.password);
		btLogin = (Button)findViewById(R.id.login);
		btExit = (Button)findViewById(R.id.exit);
		
		
	
		
		
		btExit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				finish();
	
			}
			

		
		});
		
		btLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				MsgEventHandler.sLogin(etUsername.getText().toString(), etPassword.getText().toString());
				MsgEventHandler.c_sLogin(etUsername.getText().toString(), etPassword.getText().toString());
				pd = new ProgressDialog(MainActivity.this);
				pd.setMessage("查询中...");
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
	         
	        	if( msg.what==NetworkAdapter.MSG_LOGIN){
	        		Bundle b = msg.getData();
		        	Login l = (Login) b.get("login");
		        	
		        	if(l.isLogin==Login.LOGIN_SUCCESS){
		        		ta.login = l;
		        		pd.dismiss();// 关闭ProgressDialog
		        		timer.cancel();
		            	Toast.makeText(MainActivity.this, "登录成功",Toast.LENGTH_SHORT).show();
		            	if (!ta.hb.isAlive()){
		            		ta.hb.start();
		            	}
		            	
			            Intent intent = new Intent();
			            
						intent.setClass(MainActivity.this, CarGroupListActivity.class);
						intent.putExtra("userId",l.userid);
						startActivity(intent);
						
		        	}else{
		        		pd.dismiss();// 关闭ProgressDialog
		        		timer.cancel();
		            	Toast.makeText(MainActivity.this, "登录失败",Toast.LENGTH_SHORT).show();
		            	return;
		        	}
		        	pd.dismiss();// 关闭ProgressDialog
	        		//Toast.makeText(MainActivity.this, "获取数据错误或数据库无数据",Toast.LENGTH_SHORT).show();
	        	}else if (msg.what==TIME_LIMIT){
	           	 pd.dismiss();
	        	 Toast.makeText(MainActivity.this, "设备关机或网络状况导致数据返回超时",Toast.LENGTH_SHORT).show();
	        	 return;
	         }
	        	
	            	
					
	          
	            	
	            	
	            
	         }
	        	
	 };
	 
	
	
 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		 ta = (TrackApp)getApplication();
		 ta.setHandler(handler);
	}
	
	
}
