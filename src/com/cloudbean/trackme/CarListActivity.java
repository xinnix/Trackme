package com.cloudbean.trackme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudbean.model.Car;
import com.cloudbean.model.Login;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackerUtil.MsgEventHandler;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class CarListActivity extends ListActivity {
	
	private ProgressDialog pd = null;
	private TrackApp ta=null;
	
	private   String[] carNameList=null;  
	private ListView mListView = null;
	private String carid[] = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_list);
		mListView = getListView();
		//mListView =  getListView(); 
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "你单击了"+carid[position], Toast.LENGTH_SHORT).show(); 
				Intent intent = new Intent();
				intent.setClass(CarListActivity.this,TimeChooseActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("carid", Integer.parseInt(carid[position]));
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
		});
		
		
		
		
		
		Intent intent = this.getIntent();
		
		int userId = intent.getIntExtra("userId", 0);
		
		MsgEventHandler.sGetCarInfo(userId, "");
		
		pd = new ProgressDialog(CarListActivity.this);
		pd.setMessage("用户车辆列表获取...");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
	
    //数据数据  
    
	}
	
	
	
	
	private  Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
        	if(msg.what==NetworkAdapter.MSG_SUCCESS){
        		Bundle b = msg.getData();
            	Car[] carList = (Car[]) b.getParcelableArray("carList");
            	
            	if(carList.length>0){
            		
            		carid = new String[carList.length];
            		for(int ii=0;ii<carList.length;ii++){
            			carid[ii] = carList[ii].id;
            		}
            		
            		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
            		
            		for (int i = 0; i < carList.length; i++) {
            			Map<String,Object> listItem = new HashMap<String,Object>();
            			listItem.put("img", R.drawable.car);
            			listItem.put("carId", carList[i].id);
            			listItem.put("name", carList[i].ipAddress);	
            			listItems.add(listItem);
            		}
            		
            		SimpleAdapter adapter = new SimpleAdapter(CarListActivity.this, listItems, R.layout.custom_list, 
            				new String[]{"img","carId","name"}, 
            				new int[]{R.id.carImg,R.id.carId ,R.id.carName});
            		
            		
            		//ArrayAdapter<String> aa=new ArrayAdapter<String>(CarListActivity.this,android.R.layout.simple_list_item_1,data);
            		setListAdapter(adapter);  
            		pd.dismiss();// 关闭ProgressDialog
                	
            	}else{
            		
                	Toast.makeText(getApplicationContext(), "获取失败",Toast.LENGTH_SHORT).show();
            	}
        	}else{
        		Toast.makeText(getApplicationContext(), "获取数据错误或数据库无数据",Toast.LENGTH_SHORT).show();
        	}
         
        	
            	
				
          
            	
            	
            
         }
        	
 };
 
 
	 protected void onResume() {  
	     super.onResume();  
	     ta = (TrackApp)getApplication();
	     ta.setHandler(handler);
	     Log.i("test", "onResume");
	}  

 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.car_list, menu);
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
