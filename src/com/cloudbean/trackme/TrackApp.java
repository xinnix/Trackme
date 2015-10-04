package com.cloudbean.trackme;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudbean.model.Alarm;
import com.cloudbean.model.Car;
import com.cloudbean.model.CarGroup;
import com.cloudbean.model.Login;
import com.cloudbean.model.User;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.HeartBeat;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackme.server.NetWorkService;

import android.app.Application;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

public class TrackApp extends Application{
	
	
	public static Handler curHandler = null;
	
	public static User  user = null;
	public static Login  login = null;
	public static Car[] carList = null;
	public static CarGroup[] carGroupList=null;
	public static Car currentCar = null;
	public static String curUsername = null;
	public static String curPassword = null;
	
	public static String curCommand = null;
	
	
	public static boolean isLogin = false;
	public static boolean isMute = false;
	public static List<Alarm> alarmList = new ArrayList<Alarm>();
	
	
	public static NetWorkService nws = null;
	public static String dServerAddr  = "61.145.122.143:4519";
	public static String cServerAddr  = "61.145.122.143:4508";
	public static AudioManager mgr;
	
	private static SoundPool pool;
	private static Map<String, Integer> poolMap;
	
	
	public static Handler getHandler() {
		return curHandler;
	}


	public static void setHandler(Handler handler) {
		curHandler = handler;
//		na.handler = this.handler;
//		nac.handler = this.handler;
		
	
	}
	
	
	public void initSound(){
		mgr = (AudioManager)this.getSystemService(this.AUDIO_SERVICE);   

        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);   

        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);       

       float volume = streamVolumeCurrent/streamVolumeMax; 
		poolMap = new HashMap<String, Integer>();
        // 实例化SoundPool，大小为3
		pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        // 装载音频进音频池，并且把ID记录在Map中
		poolMap.put("alarm", pool.load(this, R.raw.alarmsound, 1));
	}
	
	public static void playAlarmSound(){	
		
		pool.play(poolMap.get("alarm"), 1.0f, 1.0f, 0, 1, 1);
	}
	
	public static void setMute(boolean state){	
		mgr.setStreamMute(AudioManager.STREAM_MUSIC, state);
	}
	


	public void onCreate()
    {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        initSound();
        
    }
	
	
	
	
}
