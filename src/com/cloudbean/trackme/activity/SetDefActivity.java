package com.cloudbean.trackme.activity;

import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;
import com.cloudbean.trackme.R.menu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SetDefActivity extends BaseActivity {
	private Button btSetDef = null;
	private Button btCancelDef = null;
	
	private String curCommand = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_def, menu);
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
		setContentView(R.layout.activity_set_def);
		btSetDef = (Button)findViewById(R.id.set_def);
		btCancelDef = (Button)findViewById(R.id.cancel_def);
		btSetDef.setOnClickListener(this);
		btCancelDef.setOnClickListener(this);
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.set_def:
			MsgEventHandler.c_sSetDef(TrackApp.currentCar, "01");
			curCommand = btSetDef.getText().toString();
			showProgressDialog(curCommand+"发送中...");
			timerStart();
			break;
		case R.id.cancel_def:
			MsgEventHandler.c_sSetDef(TrackApp.currentCar, "00");
			curCommand = btCancelDef.getText().toString();
			showProgressDialog(curCommand+"发送中...");
			timerStart();
			break;
		}
	}

	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
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
//						getData("操作成功");
					}else if(res.equals("0000")){
						showMessage("操作失败");
//						getData("操作失败");
					}else{
						showMessage("服务器回复错误");
					}
					
			 }
    	
    	}
	}
}
