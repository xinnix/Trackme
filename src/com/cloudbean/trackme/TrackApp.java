package com.cloudbean.trackme;
import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackerUtil.MsgEventHandler;

import android.app.Application;
import android.os.StrictMode;

public class TrackApp extends Application{
	
	public static NetworkAdapter na = null;
	public static CNetworkAdapter nac = null;
	
	public void onCreate()
    {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        na = new NetworkAdapter("61.145.122.143",4519);
    	nac = new CNetworkAdapter("61.145.122.143",4508);
    	
		MsgEventHandler.config(na, nac);
        
    }
	
}
