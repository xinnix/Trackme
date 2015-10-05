package com.cloudbean.trackme.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cloudbean.model.Alarm;
import com.cloudbean.model.Car;
import com.cloudbean.model.CarGroup;
import com.cloudbean.model.Login;
import com.cloudbean.model.User;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.trackme.AppManager;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.R.drawable;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



 
public class MenuActivity extends BaseActivity {
	private GridView gridview;
	private TextView tvCarid;
	public String[] menuItems={"�豸�б�","λ�û�ȡ","��ʷ�켣","�����鿴","��·����","�������","ָ���·�","ע��"};
	public int[] icon = {R.drawable.menu_carlist,R.drawable.menu_trace,R.drawable.menu_replay,R.drawable.menu_alarm,R.drawable.menu_command,R.drawable.set_def,R.drawable.send_command,R.drawable.menu_exit};
	Intent intent = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        intent = this.getIntent();
        
        
        ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
        for(int i = 0;i < menuItems.length;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", icon[i]);
            map.put("ItemText", menuItems[i]);
            meumList.add(map);
        }
        SimpleAdapter saItem = new SimpleAdapter(this,
                  meumList, //����Դ
                  R.layout.menu_item, //xmlʵ��
                  new String[]{"ItemImage","ItemText"}, //��Ӧmap��Key
                  new int[]{R.id.ItemImage,R.id.ItemText});  //��ӦR��Id
 
                //���Item��������
                gridview.setAdapter(saItem);
                //��ӵ���¼�
                gridview.setOnItemClickListener(
                    new AdapterView.OnItemClickListener()
                    {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3)
                        {
                        	
                        	switch (position){
                        	case 0:
                        		intent.setClass(MenuActivity.this, CarGroupListActivity.class);
                        		break;
                        	case 1:
                        		
                        		intent.setClass(MenuActivity.this, TraceActivity.class);
                        		break;
                        	case 2:
                        		intent.setClass(MenuActivity.this, TimeChooseActivity.class);
                        		break;
                        	case 3:
                        		intent.setClass(MenuActivity.this, AlarmListActivity.class);
                        		break;
                        	case 4:
                        		intent.setClass(MenuActivity.this, SetCommandActivity.class);
                        		break;
                        	case 5:
                        		intent.setClass(MenuActivity.this, SetDefActivity.class);
                        		break;
                        	case 6:
                        		intent.setClass(MenuActivity.this,OtherCommandActivity.class);
                        		break;
                        	case 7:
                        		intent.setClass(MenuActivity.this, MainActivity.class);
                        		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
                        		initApp();
                        		AppManager.getAppManager().finishAllActivity();
//                        		System.exit(0);
                        		break;
                        		
                        	}
                        	startActivity(intent);
                        }

						
                    }
                );
    }
    

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK )
		{
				// �����˳��Ի���
			AlertDialog isExit = new AlertDialog.Builder(this).create();
				// ���öԻ������
			isExit.setTitle("ϵͳ��ʾ");
				// ���öԻ�����Ϣ
			isExit.setMessage("ȷ��Ҫ�˳���");
				// ���ѡ��ť��ע�����
			isExit.setButton("ȷ��", listener);
			isExit.setButton2("ȡ��", listener);
				// ��ʾ�Ի���
			isExit.show();
	
		}else if (keyCode == KeyEvent.KEYCODE_HOME){
			moveTaskToBack(true);  
			return true; 
		}
		
		return false;
		
	}
	/**�����Ի��������button����¼�*/
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
				case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����
					initApp();
					System.exit(0);
				break;
				case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���
				break;
				default:
				break;
			}
		}
	};	

    private void initApp(){
    	TrackApp.isLogin = false;
		TrackApp.alarmList= new ArrayList<Alarm>();
		TrackApp.curHandler = null;
		TrackApp.user = null;
		TrackApp.login = null;
		TrackApp.carList = null;
		TrackApp.carGroupList=null;
		TrackApp.currentCar = null;
		TrackApp.curUsername = null;
		TrackApp.curPassword = null;
		TrackApp.na.reconnThread.interrupt();
		TrackApp.cna.reconnThread.interrupt();
	
		
		TrackApp.na = null;
		TrackApp.cna = null;
    }
    
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {  
//        if (keyCode == KeyEvent.KEYCODE_BACK) {  
//            moveTaskToBack(true);  
//            return true;  
//        }  
//        return super.onKeyDown(keyCode, event);  
//    }  
    
	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_menu);
		gridview = (GridView) findViewById(R.id.GridView);
		tvCarid = (TextView) findViewById(R.id.tv_menu_carid);
		
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		tvCarid.setText(TrackApp.currentCar.name);
	}


	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		
	}
}