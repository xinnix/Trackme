package com.cloudbean.trackme;

import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.HeartBeat;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NetWorkService extends Service {
	public NetworkAdapter na;
	public CNetworkAdapter cna;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		 na = new NetworkAdapter("61.145.122.143",4519);
		 cna = new CNetworkAdapter("61.145.122.143",4508);
		 na.start();
		 cna.start();
		 new HeartBeat().start();
		 MsgEventHandler.config(na, cna);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

}
