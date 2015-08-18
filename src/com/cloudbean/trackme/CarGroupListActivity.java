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
	//// 为列表绑定数据源
	
	private int child_groupId = -1;
	private int child_childId = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cargroup);
		expandableListView = (ExpandableListView) findViewById(R.id.grouplist);
		// 设置默认图标为不显示状态
		expandableListView.setGroupIndicator(null);
		
		// 设置一级item点击的监听器
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
			//	group_checked[groupPosition] = group_checked[groupPosition] + 1;
				// 刷新界面
				((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
				return false;
			}
		});

		// 设置二级item点击的监听器
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// 将被点击的一丶二级标签的位置记录下来
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
				// 刷新界面
				
				
				((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
				return false;
			}
		});
		
		
		
		
		Intent intent = this.getIntent();
		int userId = intent.getIntExtra("userId", 0);
		MsgEventHandler.sGetCarInfo(userId,"");
		MsgEventHandler.sGetCarGroup(userId,"");
		
		pd = new ProgressDialog(CarGroupListActivity.this);
		pd.setMessage("用户车辆列表获取...");
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
	
		// 一级标签上的状态图片数据源
		int[] group_state_array = new int[] { R.drawable.group_down,R.drawable.group_up };

		// 重写ExpandableListAdapter中的各个方法
		/**
		 * 获取一级标签总数
		 */
		@Override
		public int getGroupCount() {
			return carGroupArry.size();
		}

		/**
		 * 获取一级标签内容
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return carGroupArry.get(groupPosition).vehGroupName;
		}

		/**
		 * 获取一级标签的ID
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * 获取一级标签下二级标签的总数
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return carTable.get(groupPosition).size();
		}

		/**
		 * 获取一级标签下二级标签的内容
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return carTable.get(groupPosition).get(childPosition).name;
		}

		/**
		 * 获取二级标签的ID
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * 指定位置相应的组视图
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * 对一级标签进行设置
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// 为视图对象指定布局
			convertView = (LinearLayout) LinearLayout.inflate(getBaseContext(),R.layout.group_layout,null);

			RelativeLayout myLayout = (RelativeLayout) convertView.findViewById(R.id.group_layout);

			/**
			 * 声明视图上要显示的控件
			 */
			// 新建一个TextView对象，用来显示一级标签上的标题信息
			TextView group_title = (TextView) convertView.findViewById(R.id.group_title);
			// 新建一个TextView对象，用来显示一级标签上的大体描述的信息
			ImageView group_state = (ImageView) convertView.findViewById(R.id.group_state);
			/**
			 * 设置相应控件的内容
			 */
			// 设置标题上的文本信息
			group_title.setText(carGroupArry.get(groupPosition).vehGroupName);
			// 设置整体描述上的文本信息

//			if (group_checked[groupPosition] % 2 == 1) {
//				// 设置默认的图片是选中状态
//				group_state.setBackgroundResource(group_state_array[1]);
//				myLayout.setBackgroundResource(R.drawable.text_item_top_bg);
//			} else {
//				for (int test : group_checked) {
//					if (test == 0 || test % 2 == 0) {
//						myLayout.setBackgroundResource(R.drawable.text_item_bg);
//						// 设置默认的图片是未选中状态
//						group_state.setBackgroundResource(group_state_array[0]);
//					}
//				}
//			}
			// 返回一个布局对象
			return convertView;
		}

		/**
		 * 对一级标签下的二级标签进行设置
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			// 为视图对象指定布局
			
			convertView = (RelativeLayout) RelativeLayout.inflate(getBaseContext(), R.layout.custom_list, null);
			
			/**
			 * 声明视图上要显示的控件
			 */
			// 新建一个TextView对象，用来显示具体内容
			TextView child_carName = (TextView) convertView.findViewById(R.id.carName);
			TextView child_carId = (TextView) convertView.findViewById(R.id.carId);
			ImageView child_carImg = (ImageView) convertView.findViewById(R.id.carImg);
			/**
			 * 设置相应控件的内容
			 */
			// 设置要显示的文本信息
			child_carName.setText(carTable.get(groupPosition).get(childPosition).name);
			child_carId.setText(carTable.get(groupPosition).get(childPosition).id);
			child_carImg.setImageResource(R.drawable.car);
			// 判断item的位置是否相同，如相同，则表示为选中状态，更改其背景颜色，如不相同，则设置背景色为白色
			if (child_groupId == groupPosition
					&& child_childId == childPosition) {
				// 设置背景色为绿色
				// convertView.setBackgroundColor(Color.GREEN);
			} else {
				// 设置背景色为白色
				// convertView.setBackgroundColor(Color.WHITE);
			}
			// 返回一个布局对象
			return convertView;
		}

		/**
		 * 当选择子节点的时候，调用该方法
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	};
	
	
	private  Handler handler = new Handler() {  
		int flag = 0;//防止其他消息导致数据重复刷新
		@Override  
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
        	
        	
        	
        	if(msg.what==NetworkAdapter.MSG_CARINFO){
        		Bundle b = msg.getData();
            	carList = (Car[]) b.getParcelableArray("carList");
            	
            	ta.carList = carList;
            	
            	if(carList.length>0){
            		// 为列表绑定数据源
            		flag=0;
                	
            	}else{
            		
                	Toast.makeText(CarGroupListActivity.this, "获取失败",Toast.LENGTH_SHORT).show();
                	return;
            	}
        	}else if(msg.what==NetworkAdapter.MSG_CARGROUPINFO){
        		Bundle b = msg.getData();
            	carGroupList = (CarGroup[]) b.getParcelableArray("carGroupList");
            	
            	if(carGroupList.length>0){
            		// 为列表绑定数据源
            		flag=0;
                	
            	}else{
            		
                	Toast.makeText(CarGroupListActivity.this, "获取失败",Toast.LENGTH_SHORT).show();
                	return;
            	}
//            	Toast.makeText(CarGroupListActivity.this, "获取数据错误或数据库无数据",Toast.LENGTH_SHORT).show();
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
        		pd.dismiss();// 关闭ProgressDialog
        	}
       	
            
         }
        	
 };

}


