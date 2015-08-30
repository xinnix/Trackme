package com.cloudbean.trackme;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.cloudbean.model.Car;
import com.cloudbean.model.CarGroup;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class CarGroupListActivity extends BaseActivity{
	
	
	
//	private   String[] carNameList=null; 
//	private String carid[] = null;
	
	
	private List<CarGroup> carGroupArry = new ArrayList<CarGroup>();
	private List<List<Car>> carTable = new ArrayList<List<Car>>();
	

	private ExpandableListView expandableListView;
	
	
	
	
	private Car[] carList = null;
	private CarGroup[] carGroupList = null;
	//// Ϊ�б������Դ
	
	private int child_groupId = -1;
	private int child_childId = -1;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_cargroup);
//		expandableListView = (ExpandableListView) findViewById(R.id.grouplist);
//		// ����Ĭ��ͼ��Ϊ����ʾ״̬
//		expandableListView.setGroupIndicator(null);
//		expandableListView.setAdapter(adapter);
		
		// ����һ��item����ļ�����
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
			//	group_checked[groupPosition] = group_checked[groupPosition] + 1;
				// ˢ�½���
				((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
				return false;
			}
		});

		// ���ö���item����ļ�����
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// ���������һؼ������ǩ��λ�ü�¼����
				child_groupId = groupPosition;
				child_childId = childPosition;
				
				
				Intent intent = new Intent();
				intent.setClass(CarGroupListActivity.this,MenuActivity.class);
				Bundle bundle = new Bundle();
				Car car  = carTable.get(child_groupId).get(child_childId);
				
				TrackApp.currentCar = car;
				bundle.putInt("carid", Integer.parseInt(car.id));
				intent.putExtras(bundle);
				startActivity(intent);
				// ˢ�½���
				
				
				((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
				return false;
			}
		});
		
		
		
		
		Intent intent = this.getIntent();
		int userId = intent.getIntExtra("userId", 0);
		MsgEventHandler.sGetCarInfo(userId,"");
		MsgEventHandler.sGetCarGroup(userId,"");
		
		showProgressDialog("��ȡ�����б�");
		timerStart();
		
	}
	
	
	 protected void onResume() {  
	     super.onResume();  
	     
	     if(TrackApp.carList!=null&&TrackApp.carGroupList!=null){
	    	 dismissProgressDialog();
		     initCarTable(TrackApp.carList,TrackApp.carGroupList);
		     ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
	     }
	    
	     Log.i("test", "onResume");
	}  
	 
	 
	 
	final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
		
		//private String[] carGroupTitleArry = null;
	
		// һ����ǩ�ϵ�״̬ͼƬ����Դ
		int[] group_state_array = new int[] { R.drawable.group_down,R.drawable.group_up };

		// ��дExpandableListAdapter�еĸ�������
		/**
		 * ��ȡһ����ǩ����
		 */
		@Override
		public int getGroupCount() {
			return carGroupArry.size();
		}

		/**
		 * ��ȡһ����ǩ����
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return carGroupArry.get(groupPosition).vehGroupName;
		}

		/**
		 * ��ȡһ����ǩ��ID
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * ��ȡһ����ǩ�¶�����ǩ������
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return carTable.get(groupPosition).size();
		}

		/**
		 * ��ȡһ����ǩ�¶�����ǩ������
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return carTable.get(groupPosition).get(childPosition).name;
		}

		/**
		 * ��ȡ������ǩ��ID
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * ָ��λ����Ӧ������ͼ
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * ��һ����ǩ��������
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// Ϊ��ͼ����ָ������
			convertView = (LinearLayout) LinearLayout.inflate(getBaseContext(),R.layout.group_layout,null);

			RelativeLayout myLayout = (RelativeLayout) convertView.findViewById(R.id.group_layout);

			/**
			 * ������ͼ��Ҫ��ʾ�Ŀؼ�
			 */
			// �½�һ��TextView����������ʾһ����ǩ�ϵı�����Ϣ
			TextView group_title = (TextView) convertView.findViewById(R.id.group_title);
			// �½�һ��TextView����������ʾһ����ǩ�ϵĴ�����������Ϣ
			ImageView group_state = (ImageView) convertView.findViewById(R.id.group_state);
			/**
			 * ������Ӧ�ؼ�������
			 */
			// ���ñ����ϵ��ı���Ϣ
			group_title.setText(carGroupArry.get(groupPosition).vehGroupName);
			group_state.setBackgroundResource(R.drawable.group_state);
			// �������������ϵ��ı���Ϣ

//			if (group_checked[groupPosition] % 2 == 1) {
//				// ����Ĭ�ϵ�ͼƬ��ѡ��״̬
//				group_state.setBackgroundResource(group_state_array[1]);
//				myLayout.setBackgroundResource(R.drawable.text_item_top_bg);
//			} else {
//				for (int test : group_checked) {
//					if (test == 0 || test % 2 == 0) {
//						myLayout.setBackgroundResource(R.drawable.text_item_bg);
//						// ����Ĭ�ϵ�ͼƬ��δѡ��״̬
//						group_state.setBackgroundResource(group_state_array[0]);
//					}
//				}
//			}
			// ����һ�����ֶ���
			return convertView;
		}

		/**
		 * ��һ����ǩ�µĶ�����ǩ��������
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			// Ϊ��ͼ����ָ������
			
			convertView = (RelativeLayout) RelativeLayout.inflate(getBaseContext(), R.layout.custom_list, null);
			
			/**
			 * ������ͼ��Ҫ��ʾ�Ŀؼ�
			 */
			// �½�һ��TextView����������ʾ��������
			TextView child_carName = (TextView) convertView.findViewById(R.id.carName);
			TextView child_carDevId = (TextView) convertView.findViewById(R.id.carDevId);
			TextView child_carDevType = (TextView) convertView.findViewById(R.id.carDevType);
			ImageView child_carImg = (ImageView) convertView.findViewById(R.id.carImg);
			/**
			 * ������Ӧ�ؼ�������
			 */
			// ����Ҫ��ʾ���ı���Ϣ
			child_carDevType.setText(carTable.get(groupPosition).get(childPosition).devtype);
			child_carName.setText(carTable.get(groupPosition).get(childPosition).name);
			child_carDevId.setText(carTable.get(groupPosition).get(childPosition).devId);
			child_carImg.setImageResource(carTable.get(groupPosition).get(childPosition).getLastState()!=null?R.drawable.car_online:R.drawable.car_offline);
			// �ж�item��λ���Ƿ���ͬ������ͬ�����ʾΪѡ��״̬�������䱳����ɫ���粻��ͬ�������ñ���ɫΪ��ɫ
			if (child_groupId == groupPosition
					&& child_childId == childPosition) {
				// ���ñ���ɫΪ��ɫ
				// convertView.setBackgroundColor(Color.GREEN);
			} else {
				// ���ñ���ɫΪ��ɫ
				// convertView.setBackgroundColor(Color.WHITE);
			}
			// ����һ�����ֶ���
			return convertView;
		}

		/**
		 * ��ѡ���ӽڵ��ʱ�򣬵��ø÷���
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	};
	
	
	private  Handler handler = new Handler() {  
		int flag = 0;//��ֹ������Ϣ���������ظ�ˢ��
		@Override  
        public void handleMessage(Message msg) {// handler���յ���Ϣ��ͻ�ִ�д˷���  
        	
        	
        	
//        	if(msg.what==NetworkAdapter.MSG_CARINFO){
//        		Bundle b = msg.getData();
//            	carList = (Car[]) b.getParcelableArray("carList");
//            	
//            	TrackApp.carList = carList;
//            	
//            	if(carList.length>0){
//            		// Ϊ�б������Դ
//            		flag=0;
//                	
//            	}else{
//            		
//                	Toast.makeText(CarGroupListActivity.this, "��ȡʧ��",Toast.LENGTH_SHORT).show();
//                	return;
//            	}
//        	}else if(msg.what==NetworkAdapter.MSG_CARGROUPINFO){
//        		Bundle b = msg.getData();
//        		TrackApp.carGroupList = (CarGroup[]) b.getParcelableArray("carGroupList");
//            	
//            	if(TrackApp.carGroupList.length>0){
//            		// Ϊ�б������Դ
//            		flag=0;
//                	
//            	}else{
//            		
//                	Toast.makeText(CarGroupListActivity.this, "��ȡʧ��",Toast.LENGTH_SHORT).show();
//                	return;
//            	}
////            	Toast.makeText(CarGroupListActivity.this, "��ȡ���ݴ�������ݿ�������",Toast.LENGTH_SHORT).show();
//        	}else if(msg.what==CNetworkAdapter.MSG_POSITION){
//        		
//        		Bundle b = msg.getData();
//        		if(TrackApp.carList!=null){
//        			updateCarAlive(TrackApp.carList,b.getString("devid"));
//        		}
//        		if(TrackApp.carList!=null&&TrackApp.carGroupList!=null){
//        			initCarTable(TrackApp.carList,TrackApp.carGroupList);
//        		}
//        		
//        		((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
//        		
//        	}else if (msg.what==TIME_LIMIT){
//        	
//	           	 pd.dismiss();
//	        	 Toast.makeText(CarGroupListActivity.this, "�豸�ػ�������״���������ݷ��س�ʱ",Toast.LENGTH_SHORT).show();
//	        	 return;
//	        }
//        	
//        	
//        	if((TrackApp.carList!=null)&&(TrackApp.carGroupList!=null)&&(flag==0)){
//        		
//        		initCarTable(TrackApp.carList,TrackApp.carGroupList);
//        		((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
//        		flag=1;
//        		pd.dismiss();// �ر�ProgressDialog
//        		timer.cancel();
//        	}
//       	
//            
         }
        	
 };
 
 
 	private void initCarTable(Car[] carList,CarGroup[] carGroupList){
 		carGroupArry.clear();
 		carTable.clear();
 		
 		for (int ii=0; ii<carGroupList.length;ii++){
			carGroupArry.add(carGroupList[ii]);
		}
		
		
		
		for (int ii=0; ii<carGroupList.length;ii++){
			List<Car> tempCarList= new ArrayList<Car>();
			
			for (int jj=0; jj<carList.length;jj++){
				if(Integer.parseInt(carList[jj].carGroupId)==carGroupList[ii].vehGroupID)
					tempCarList.add(carList[jj]);
			}
			
			
			carTable.add(tempCarList);
		}
 	}
 	
 	
	 private void updateCarAlive(Car[] carList,String devId){
		 for (int ii=0;ii<carList.length;ii++){
			 if(carList[ii].devId.equals(devId)){
				 carList[ii].setAlive(true);
				 return;
			 }
		 }
	 }


	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_cargroup);
		expandableListView = (ExpandableListView) findViewById(R.id.grouplist);
		// ����Ĭ��ͼ��Ϊ����ʾ״̬
		expandableListView.setGroupIndicator(null);
		expandableListView.setAdapter(adapter);
	}


	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
	    	if(msg.what==NetworkAdapter.MSG_CARINFO){
			Bundle b = msg.getData();
	    	carList = (Car[]) b.getParcelableArray("carList");
	    	
	    	TrackApp.carList = carList;
	    	
	    	if(carList.length>0){
	    		// Ϊ�б������Դ
	    	}else{
	    		showMessage("��ȡʧ��");
	        	return;
	    	}
		}else if(msg.what==NetworkAdapter.MSG_CARGROUPINFO){
			Bundle b = msg.getData();
			TrackApp.carGroupList = (CarGroup[]) b.getParcelableArray("carGroupList");
	    	
	    	if(TrackApp.carGroupList.length>0){
	    		// Ϊ�б������Դ
	    		
	        	
	    	}else{
	    		
	    		showMessage("��ȡʧ��");
	        	return;
	    	}
	//    	Toast.makeText(CarGroupListActivity.this, "��ȡ���ݴ�������ݿ�������",Toast.LENGTH_SHORT).show();
		}else if(msg.what==CNetworkAdapter.MSG_POSITION){
			
			Bundle b = msg.getData();
			if(TrackApp.carList!=null){
				updateCarAlive(TrackApp.carList,b.getString("devid"));
			}
			if(TrackApp.carList!=null&&TrackApp.carGroupList!=null){
				initCarTable(TrackApp.carList,TrackApp.carGroupList);
			}
			
			((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
			
		}else if (msg.what==TIME_OUT){
		
	       	 dismissProgressDialog();
	    	 showMessage("�豸�ػ�������״���������ݷ��س�ʱ");
	    	 return;
	    }
		
		
		if((TrackApp.carList!=null)&&(TrackApp.carGroupList!=null)){
			
			initCarTable(TrackApp.carList,TrackApp.carGroupList);
			((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
			
			dismissProgressDialog();// �ر�ProgressDialog
			timerStop();
		}
		

	}

}


