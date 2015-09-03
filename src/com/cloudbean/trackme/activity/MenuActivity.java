package com.cloudbean.trackme.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.R.drawable;
import com.cloudbean.trackme.R.id;
import com.cloudbean.trackme.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



 
public class MenuActivity extends BaseActivity {
	private GridView gridview;
	private TextView tvCarid;
	public String[] menuItems={"车辆列表","实时定位","历史轨迹","报警列表","指令下发","注销"};
	public int[] icon = {R.drawable.menu_carlist,R.drawable.menu_trace,R.drawable.menu_replay,R.drawable.menu_alarm,R.drawable.menu_command,R.drawable.menu_exit};
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
                  meumList, //数据源
                  R.layout.menu_item, //xml实现
                  new String[]{"ItemImage","ItemText"}, //对应map的Key
                  new int[]{R.id.ItemImage,R.id.ItemText});  //对应R的Id
 
                //添加Item到网格中
                gridview.setAdapter(saItem);
                //添加点击事件
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
                        		intent.setClass(MenuActivity.this, MainActivity.class);
                        		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
                        		
                        		break;
                        		
                        	}
                        	startActivity(intent);
                        }

						
                    }
                );
    }
	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_menu);
		gridview = (GridView) findViewById(R.id.GridView);
		tvCarid = (TextView) findViewById(R.id.tv_menu_carid);
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