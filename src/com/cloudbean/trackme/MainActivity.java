package com.cloudbean.trackme;

import com.cloudbean.trackerUtil.MsgEventHandler;
import com.cloudbean.trackme.TrackApp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
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
	
	

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		etUsername = (EditText)findViewById(R.id.username);
		etPassword = (EditText)findViewById(R.id.password);
		btLogin = (Button)findViewById(R.id.login);
		btExit = (Button)findViewById(R.id.exit);
		
		
		TrackApp ta = (TrackApp)getApplication();
		ta.na.setHandler(handler);
		ta.nac.setHandler(handler);
		ta.na.start();
		ta.nac.start();
		
		
		btExit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				System.exit(0);
	
			}
			

		
		});
		
		btLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				MsgEventHandler.sLogin(etUsername.getText().toString(), etPassword.getText().toString());
				pd = new ProgressDialog(MainActivity.this);
				pd.setMessage("查询中...");
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.show();
				
				
				
				
			}
			

		
		});
		
	}
	
	
	 private  Handler handler = new Handler() {  
	        @Override  
	        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
	            switch (msg.what){
	            case 0:
	            	pd.dismiss();// 关闭ProgressDialog
		            Intent intent = new Intent();
					intent.setClass(MainActivity.this, MapActivity.class);
					startActivity(intent);
					break;
	            case 1:
	            	Toast.makeText(getApplicationContext(), "登录失败",Toast.LENGTH_SHORT).show();
	            	
	            
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
	
	
}
