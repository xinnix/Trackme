package com.cloudbean.trackme;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.cloudbean.model.Car;
import com.cloudbean.model.CarGroup;
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

public class CarGroupListActivity extends Activity{
	
	
	private ProgressDialog pd = null;
	private TrackApp ta=null;
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
		setContentView(R.layout.activity_cargroup);
		expandableListView = (ExpandableListView) findViewById(R.id.grouplist);
		// ����Ĭ��ͼ��Ϊ����ʾ״̬
		expandableListView.setGroupIndicator(null);
		
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
				
				ta.currentCar = car;
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
		
		pd = new ProgressDialog(CarGroupListActivity.this);
		pd.setMessage("�û������б��ȡ...");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		
	}
	
	
	 protected void onResume() {  
	     super.onResume();  
	     ta = (TrackApp)getApplication();
	     ta.setHandler(handler);
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
			TextView child_carId = (TextView) convertView.findViewById(R.id.carId);
			ImageView child_carImg = (ImageView) convertView.findViewById(R.id.carImg);
			/**
			 * ������Ӧ�ؼ�������
			 */
			// ����Ҫ��ʾ���ı���Ϣ
			child_carName.setText(carTable.get(groupPosition).get(childPosition).name);
			child_carId.setText(carTable.get(groupPosition).get(childPosition).id);
			child_carImg.setImageResource(R.drawable.car);
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
        	
        	
        	
        	if(msg.what==NetworkAdapter.MSG_CARINFO){
        		Bundle b = msg.getData();
            	carList = (Car[]) b.getParcelableArray("carList");
            	
            	ta.carList = carList;
            	
            	if(carList.length>0){
            		// Ϊ�б������Դ
            		flag=0;
                	
            	}else{
            		
                	Toast.makeText(CarGroupListActivity.this, "��ȡʧ��",Toast.LENGTH_SHORT).show();
                	return;
            	}
        	}else if(msg.what==NetworkAdapter.MSG_CARGROUPINFO){
        		Bundle b = msg.getData();
            	carGroupList = (CarGroup[]) b.getParcelableArray("carGroupList");
            	
            	if(carGroupList.length>0){
            		// Ϊ�б������Դ
            		flag=0;
                	
            	}else{
            		
                	Toast.makeText(CarGroupListActivity.this, "��ȡʧ��",Toast.LENGTH_SHORT).show();
                	return;
            	}
//            	Toast.makeText(CarGroupListActivity.this, "��ȡ���ݴ�������ݿ�������",Toast.LENGTH_SHORT).show();
        	}
        	
        	
        	if((carList!=null)&&(carGroupList!=null)&&(flag==0)){
        		
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
        		expandableListView.setAdapter(adapter);
        		flag=1;
        		pd.dismiss();// �ر�ProgressDialog
        	}
       	
            
         }
        	
 };

}


