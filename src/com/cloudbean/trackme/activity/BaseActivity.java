package com.cloudbean.trackme.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.cloudbean.model.Login;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackme.AppManager;
import com.cloudbean.trackme.TrackApp;
import com.cloudbean.trackme.server.NetWorkService;
import com.cloudbean.trackme.server.NetWorkService.NetworkBinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

public abstract class BaseActivity extends Activity implements OnClickListener {
	 
	private static final int ACTIVITY_RESUME = 0;
	private static final int ACTIVITY_STOP = 1;
	private static final int ACTIVITY_PAUSE = 2;
	private static final int ACTIVITY_DESTROY = 3;
	
	
	protected static final int TIME_OUT = 0x5001;
	private static final int TIME_LIMIT = 10000;
	
	public int activityState;
	private boolean mAllowFullScreen = true;
	
	
	protected ProgressDialog pd = null;
	
	//NetWorkService networkService;
	
	public  Handler handler =  new Handler() {  
        // handler接收到消息后就会执行此方法  
		@Override  
        public void handleMessage(Message msg) {
			handleMsg(msg);
        }
        	
	};
	
//	
//	public ServiceConnection conn = new ServiceConnection() {
//
//		@Override
//		public void onServiceConnected(ComponentName arg0, IBinder service) {
//			// TODO Auto-generated method stub
//			networkService = ((NetWorkService.NetworkBinder)service).getService();
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName arg0) {
//			// TODO Auto-generated method stub
//			networkService = null;
//		}
//	};
	
	
	private Timer timer = null;
	
	public abstract void initWidget();
	public abstract void widgetClick(View v);
	public abstract void handleMsg(Message msg);
	
	public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		widgetClick(v);
	}
	
	public void showMessage(String msg){
	    Toast.makeText(AppManager.getAppManager().currentActivity(), msg, Toast.LENGTH_SHORT).show();
	}
	
	public void showProgressDialog(String msg){
		pd = new ProgressDialog(AppManager.getAppManager().currentActivity());
		pd.setMessage(msg);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	
	public void dismissProgressDialog(){
		if(pd!=null){
			pd.dismiss();
		}
		
	}
	
	
	public void timerStart(){
		
		timer = new Timer();
		
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				handler.sendEmptyMessage(TIME_OUT);	
			}
			
		}, TIME_LIMIT);
		
	}
	
	public void timerStop(){
		if(timer!=null){
			timer.cancel();		
		}
		
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (mAllowFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        }
        AppManager.getAppManager().addActivity(this);
//    	Intent startIntent = new Intent(this, NetWorkService.class);  
//        startService(startIntent); 
        
//      bindService(new Intent(BaseActivity.this, NetWorkService.class),conn, Context.BIND_AUTO_CREATE);
        initWidget();
	}
	
	 @Override
	    protected void onStart() {
	        super.onStart();
	        Log.i(this.getClass().getName(), "----onStart");
	    }
	 
	    @Override
	    protected void onResume() {
	        super.onResume();
	        activityState = ACTIVITY_RESUME;
	        TrackApp.curHandler = handler;
	        Log.i(this.getClass().getName(), "----onResume");
	    }
	 
	    @Override
	    protected void onStop() {
	        super.onResume();
	        activityState = ACTIVITY_STOP;
	        Log.i(this.getClass().getName(), "----onStop");
	    }
	 
	    @Override
	    protected void onPause() {
	        super.onPause();
	        activityState = ACTIVITY_PAUSE;
	        Log.i(this.getClass().getName(), "----onPause");
	    }
	 
	    @Override
	    protected void onRestart() {
	        super.onRestart();
	        Log.i(this.getClass().getName(), "----onRestart");
	    }
	 
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        activityState = ACTIVITY_DESTROY;
//	        unbindService(conn);
//	        Intent stopIntent = new Intent(this, NetWorkService.class);  
//	        stopService(stopIntent);  
	        Log.i(this.getClass().getName(), "----onDestroy");
	        AppManager.getAppManager().finishActivity(this);
	        
	        
	    }
	

	
	
}
