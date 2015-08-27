package com.cloudbean.trackme;

import java.util.Timer;
import java.util.TimerTask;

import com.cloudbean.model.Login;
import com.cloudbean.network.NetworkAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
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
	
	
	private static final int TIME_LIMIT = 0x5001;
	  
	public int activityState;
	private boolean mAllowFullScreen = true;
	
	protected TrackApp ta=null;
	protected ProgressDialog pd = null;
	public Handler handler =  new Handler() {  
        // handler���յ���Ϣ��ͻ�ִ�д˷���  
		@Override  
        public void handleMessage(Message msg) {
			handleMsg(msg);
        }
        	
	};
	
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
		pd.dismiss();
	}
	
	
	public void timerStart(){
		if(timer == null){
			timer = new Timer();
		}
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				handler.sendEmptyMessage(TIME_LIMIT);	
			}
			
		}, TIME_LIMIT);
		
	}
	
	public void timerStop(){	
		timer.cancel();		
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ta = (TrackApp)getApplication();
        if (mAllowFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE); // ȡ������
        }
        AppManager.getAppManager().addActivity(this);
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
	        Log.i(this.getClass().getName(), "----onDestroy");
	        AppManager.getAppManager().finishActivity(this);
	        
	    }
	

	
	
}
