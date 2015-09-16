package com.cloudbean.trackme.server;

import com.cloudbean.network.CNetworkAdapter;
import com.cloudbean.network.HeartBeat;
import com.cloudbean.network.MsgEventHandler;
import com.cloudbean.network.NetworkAdapter;
import com.cloudbean.trackme.TrackApp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class NetWorkService extends Service {
	public NetworkAdapter na;
	public CNetworkAdapter cna;
	public HeartBeat hb;
	private Binder binder;
	
	
	private Context context;
	
	
	private String dip;
	private String cip;
	private int dport;
	private int cport;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		binder = new NetworkBinder();
		return binder;
	}
	public class NetworkBinder extends Binder {

	   public NetWorkService getService() {
	            return NetWorkService.this;
	   }
	}
	
	private String[] decodeAddr(String addr){
		return addr.split(":");
		
	}
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		String[] d = decodeAddr(TrackApp.dServerAddr);
		String[] c = decodeAddr(TrackApp.cServerAddr);
		
		dip = d[0];
		dport = Integer.parseInt(d[1]);
		
		cip = c[0];
		cport = Integer.parseInt(c[1]);
		context = getApplicationContext();
		na = new NetworkAdapter(dip,dport);
		cna = new CNetworkAdapter(cip,cport,context);
		hb = new HeartBeat();
		MsgEventHandler.config(na, cna);
//		na.start();
//		cna.start();
		
		
//		new Thread(){	
//			public void run(){
//				while(true){
//					if(na.socket.isClosed()){
//						na.connect();
//					}
//					if(cna.socket.isClosed()){
//						cna.connect();
//					}
//					try {
//						Thread.sleep(40000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();
	}
	
	public void hreatBeat(){
		if(hb.isInterrupted())
		hb.start();	
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
			na.interrupt();
			cna.interrupt();
			na.socket.close();
			cna.socket.close();
		}catch(Exception e){
			
		}
		
		
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

}
