package com.cloudbean.trackme;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;



 
public class MenuActivity extends Activity {
	
	public String[] menuItems={"ʵʱ��λ","��ʷ�켣","�����б�","ָ���·�","ע��"};
	Intent intent = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        
        
        intent = this.getIntent();
        
        GridView gridview = (GridView) findViewById(R.id.GridView);
        ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
        for(int i = 0;i < menuItems.length;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.car);
            map.put("ItemText", menuItems[i]);
            meumList.add(map);
        }
        SimpleAdapter saItem = new SimpleAdapter(this,
                  meumList, //����Դ
                  R.layout.menu_item, //xmlʵ��
                  new String[]{"ItemImage","ItemText"}, //��Ӧmap��Key
                  new int[]{R.id.ItemImage,R.id.ItemText});  //��ӦR��Id
 
                //����Item��������
                gridview.setAdapter(saItem);
                //���ӵ���¼�
                gridview.setOnItemClickListener(
                    new AdapterView.OnItemClickListener()
                    {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3)
                        {
                        	
                        	switch (position){
                        	case 0:
                        		
                        		intent.setClass(MenuActivity.this, TraceActivity.class);
                        		break;
                        	case 1:
                        		intent.setClass(MenuActivity.this, TimeChooseActivity.class);
                        		break;
                        	case 2:
                        		intent.setClass(MenuActivity.this, AlarmListActivity.class);
                        		break;
                        	case 3:
                        		intent.setClass(MenuActivity.this, SetCommandActivity.class);
                        		break;
                        	case 4:
                        		intent.setClass(MenuActivity.this, MainActivity.class);
                        		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
                        		
                        		break;
                        		
                        	}
                        	startActivity(intent);
                        }

						
                    }
                );
    }
}