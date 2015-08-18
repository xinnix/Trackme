package com.cloudbean.trackme;
import com.cloudbean.model.Car;
import com.cloudbean.model.Login;
import com.cloudbean.model.User;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.HeartBeat;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackerUtil.MsgEventHandler;

import android.app.Application;
import android.os.Handler;
import android.os.StrictMode;

public class TrackApp extends Application{
	
	public static NetworkAdapter na = null;
	public static CNetworkAdapter nac = null;
	public HeartBeat hb = null;
	public Handler handler = null;
	
	public static User  user = null;
	public static Login  login = null;
	public static Car[] carList = null;
	
	public static Car currentCar = null;
	
	
	public Handler getHandler() {
		return handler;
	}


	public void setHandler(Handler handler) {
		this.handler = handler;
		na.handler = this.handler;
		nac.handler = this.handler;
	}


	public void onCreate()
    {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        na = new NetworkAdapter("61.145.122.143",4519);
    	nac = new CNetworkAdapter("61.145.122.143",4508);
    	na.start();
    	nac.start();
    	hb = new HeartBeat();
		MsgEventHandler.config(na, nac);
        
    }
	
}
