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
	
	
	  //��ʾ�Ի���
	    public void showWaiterAuthorizationDialog() {
	    	
	    	//LayoutInflater��������layout�ļ����µ�xml�����ļ�������ʵ����
			LayoutInflater factory = LayoutInflater.from(SetCommandActivity.this);
			//��activity_login�еĿؼ�������View��
			final View textEntryView = factory.inflate(R.layout.password_dialog, null);
	         
	        //��LoginActivity�еĿؼ���ʾ�ڶԻ�����
			alertDialog = new AlertDialog.Builder(SetCommandActivity.this)
			//��������ط�����ʧ
			.setCancelable(false)
			//�Ի���ı���
	       .setTitle("��֤")
	       //�趨��ʾ��View
	       .setView(textEntryView)
	       //�Ի����еġ���½����ť�ĵ���¼�
	       .setPositiveButton("��֤", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int whichButton) { 
	        	   
	  			//��ȡ�û�����ġ��û������������롱
	        	//ע�⣺textEntryView.findViewById����Ҫ����Ϊ����factory.inflate(R.layout.activity_login, null)��ҳ�沼�ָ�ֵ����textEntryView��
//	        	final EditText etUserName = (EditText)textEntryView.findViewById(R.id.etuserName);
	            final EditText etPassword = (EditText)textEntryView.findViewById(R.id.etPWD);
	            
	          //��ҳ��������л�õġ��û������������롱תΪ�ַ���
//	   	        String userName = etUserName.getText().toString().trim();
	   	    	String password = etPassword.getText().toString().trim();
	   	    	
	   	    	//����Ϊֹ�Ѿ�������ַ��͵��û����������ˣ����������Ǹ����Լ�����������д������
	   	    	//������һ���򵥵Ĳ��ԣ��ٶ�������û��������붼��1�������������ҳ�棨OperationActivity��
	   	    	preventDismissDialog();
	   	    	if( password.equals(TrackApp.curPassword)){
	   	    		
	   	    		dismissDialog();
	   	    	}else{
	   	    		
	   	    		Toast.makeText(SetCommandActivity.this, "������û�������", Toast.LENGTH_SHORT).show();
	   	    	}
	           }
	       })
	       //�Ի���ġ��˳��������¼�
	       .setNegativeButton("�˳�", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int whichButton) {
	        	   SetCommandActivity.this.finish();
	           }
	       })
	       
	        //�Ի���Ĵ�������ʾ
			.create();
			alertDialog.show();
		}



    /**
     * �رնԻ���
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
     * ͨ������ ��ֹ�رնԻ���
     */
    private void preventDismissDialog() {
        try {
            Field field = alertDialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //����mShowingֵ����ƭandroidϵͳ
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
			showProgressDialog("������...");
			timerStart();
			break;
		case R.id.cancel_def:
			MsgEventHandler.c_sSetDef(TrackApp.currentCar, "00");
			curCommand = btCancelDef.getText().toString();
			showProgressDialog("������...");
			timerStart();
			break;
		case R.id.discon_circuit:
			MsgEventHandler.c_sSetCircuit(TrackApp.currentCar, "0002020202");
			curCommand = btConnCircuit.getText().toString();
			showProgressDialog("������...");
			timerStart();
			break;
		case R.id.conn_circuit:
			MsgEventHandler.c_sSetCircuit(TrackApp.currentCar, "0102020202");
			curCommand = btDisconCircuit.getText().toString();
			showProgressDialog("������...");
			timerStart();
			break;
	}
		
	}


	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		// handler���յ���Ϣ��ͻ�ִ�д˷���  
        
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
						showMessage("�����ɹ�");
						getData("�����ɹ�");
					}else if(res.equals("0000")){
						showMessage("����ʧ��");
						getData("����ʧ��");
					}else{
						showMessage("�������ظ�����");
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
						showMessage("�����ɹ�");
						getData("�����ɹ�");
					}else if(res.equals("00")){
						showMessage("����ʧ��");
						getData("����ʧ��");
					}else{
						showMessage("�������ظ�����");
					}
					adapter.notifyDataSetChanged();
			 }
    	}else if (msg.what==TIME_OUT){
    		dismissProgressDialog();
           	 showMessage("�豸�ػ�������״���������ݷ��س�ʱ");
        	 return;
         }
    
		
	}

	

	
}
