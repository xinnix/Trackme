package com.cloudbean.trackme;
import com.cloudbean.model.Car;
import com.cloudbean.model.CarGroup;
import com.cloudbean.model.Login;
import com.cloudbean.model.User;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.HeartBeat;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;

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
	public static NetWorkService nws = null;
	
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
