package com.cloudbean.trackme.activity;

import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.packet.MsgGPRSParser;
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
import android.widget.ImageView;

public class SetDefActivity extends BaseActivity {
	private Button btSetDef = null;
	private Button btCancelDef = null;
	private Button btSetPowerSupply = null;
	private Button btCancelPowerSupply = null;
	private ImageView imageViewDef = null;
	private String curCommand = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_set_def);
		btSetDef = (Button)findViewById(R.id.set_def);
		btCancelDef = (Button)findViewById(R.id.cancel_def);
		btSetPowerSupply = (Button)findViewById(R.id.set_power_supply);
		btCancelPowerSupply = (Button)findViewById(R.id.cancel_power_supply);
		imageViewDef = (ImageView)findViewById(R.id.imageViewDef);
		
		btCancelPowerSupply.setOnClickListener(this);
		btSetPowerSupply.setOnClickListener(this);
		btSetDef.setOnClickListener(this);
		btCancelDef.setOnClickListener(this);
	}

	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.set_def:
			showMessage("设防命令发送");
			MsgEventHandler.c_sSetDef(TrackApp.currentCar, "01");
			TrackApp.curCommand = btSetDef.getText().toString();
//			showProgressDialog(curCommand+"发送中...");
//			timerStart();
			break;
		case R.id.cancel_def:
			showMessage("撤防命令发送");
			MsgEventHandler.c_sSetDef(TrackApp.currentCar, "00");
			TrackApp.curCommand = btCancelDef.getText().toString();
//			showProgressDialog(curCommand+"发送中...");
//			timerStart();
			break;
		case R.id.set_power_supply:
			showMessage("设置掉电报警");
			MsgEventHandler.c_sExpandCommand(TrackApp.currentCar, "01000000010000000101");
			break;
		case R.id.cancel_power_supply:
			showMessage("取消掉电报警");
			MsgEventHandler.c_sExpandCommand(TrackApp.currentCar, "01000000010000000001");
			break;
		}
	}

	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		
		if( msg.what==MsgGPRSParser.MSG_TYPE_DEF){
			
			String res = msg.getData().getString("result");
			if(res.equals("设防成功")){
				
					imageViewDef.setImageResource(R.drawable.lock);
				
				
			}else if(res.equals("撤防成功")){
				imageViewDef.setImageResource(R.drawable.lock_open);
			}
//    		timerStop();
//    		 Bundle b = msg.getData();
//    		 String devid = b.getString("devid");
//    		 int i  = devid.indexOf("f");	
//    		 devid = devid.substring(0, i);
//			 if(devid.equals(TrackApp.currentCar.devId)){
//				 	dismissProgressDialog();
//					String res =  b.getString("res");
//					if(res.equals("0100")){
//						showMessage("操作成功");
////						getData("操作成功");
//					}else if(res.equals("0000")){
//						showMessage("操作失败");
////						getData("操作失败");
//					}else{
//						showMessage("服务器回复错误");
//					}
//					
//			 }
//    	
    	}
	}
}
