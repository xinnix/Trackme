package com.cloudbean.trackme.activity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.cloudbean.model.Login;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.HeartBeat;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackme.AppManager;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.dialog.IPDialog;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;

import android.app.Activity;
import android.app.ProgressDialog;
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
				MsgEventHandler.sLogin(etUsername.getText().toString(), etPassword.getText().toString());
				MsgEventHandler.c_sLogin(etUsername.getText().toString(), etPassword.getText().toString());
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
        	Login l = (Login) b.get("login");
        	
        	if(l.isLogin==Login.LOGIN_SUCCESS){
        		TrackApp.login = l;
        		dismissProgressDialog();
        		timerStop();
        		if(ckRemPassword.isChecked()){
        			saveUserInfo(etUsername.getText().toString(),etPassword.getText().toString());
        		}
        		showMessage("登录成功");
        		networkService.hreatBeat();
        		
        		MsgEventHandler.sGetCarInfo(l.userid,"");
        		MsgEventHandler.sGetCarGroup(l.userid,"");
        		showProgressDialog("加载基础数据");
        		TrackApp.curPassword = etPassword.getText().toString();
	            
				
        	}else{
        		dismissProgressDialog();
        		timerStop();
            	showMessage("登录失败");
            	return;
        	}
        	dismissProgressDialog();// 关闭ProgressDialog
    		//Toast.makeText(MainActivity.this, "获取数据错误或数据库无数据",Toast.LENGTH_SHORT).show();
    	}else if(msg.what == NetworkAdapter.MSG_CARGROUPINFO){
    		flag++;
    		
    	}else if(msg.what == NetworkAdapter.MSG_CARINFO){
    		flag++;
		
    	}else if (msg.what==TIME_OUT){
    		dismissProgressDialog();
       	 	showMessage("设备关机或网络状况导致数据返回超时");
       	 	return;
		}
		
		if(flag == 2){
			dismissProgressDialog();
			MsgEventHandler.c_sGetAllLastPosition();
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, MenuActivity.class);
			intent.putExtra("userId",TrackApp.login.userid);
			startActivity(intent);
		}
	}
	
	
	
	
}
