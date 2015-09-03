package com.cloudbean.trackme;
import java.util.ArrayList;
import java.util.List;

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
	public static String curPassword = null;
	
	
	public static List<Alarm> alarmList = new ArrayList<Alarm>();
	
	
	public static NetWorkService nws = null;
	public static String dServerAddr  = "61.145.122.143:4519";
	public static String cServerAddr  = "61.145.122.143:4508";
	
	
	
	public static Handler getHandler() {
		return curHandler;
	}


	public static void setHandler(Handler handler) {
		curHandler = handler;
//		na.handler = this.handler;
//		nac.handler = this.handler;
		
	
	}


	public void onCreate()
    {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
      
        
    }
	
	
	
	
}
