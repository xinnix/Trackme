package com.cloudbean.network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.cloudbean.model.Alarm;
import com.cloudbean.model.Car;
import com.cloudbean.model.CarGroup;
import com.cloudbean.model.Login;
import com.cloudbean.model.Track;
import com.cloudbean.packet.DPacketParser;
import com.cloudbean.trackerUtil.ByteHexUtil;
import com.cloudbean.trackme.TrackApp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class NetworkAdapter extends BaseNetworkAdapter {


	
	public Message msg = null;
	public Bundle bundle =null;

	public static int MSG_FAIL = 0x1000;
	public static int MSG_LOGIN = 0x1001;
	public static int MSG_CARINFO = 0x1002;
	public static int MSG_CARGROUPINFO = 0x1003;
	public static int MSG_TRACK = 0x1004;
	public static int MSG_ALARM = 0x1005;
	
	 public NetworkAdapter(final String serverIP,final int port){
		 super(serverIP,port);
		 connect();
	}
	
	 public NetworkAdapter(byte[] packet){
		 super(packet);
	 }

	 public byte[] preParser(InputStream inputStream) throws Exception{
		 DataInputStream dis = new DataInputStream((new BufferedInputStream(inputStream)));
		 ByteArrayOutputStream  bos = new ByteArrayOutputStream();
		 try{
			 int header = dis.readInt();
			 int datalen = dis.readInt();
			 byte[] packet = new byte[datalen-8];
			 dis.readFully(packet);
			 bos.write(ByteHexUtil.intToByte(header));
			 bos.write(ByteHexUtil.intToByte(datalen));
			 bos.write(packet);
			 
			 
		 }catch(Exception e){
			 throw e;
		 }
		 return bos.toByteArray();
	 }
//	 public void run(){
//		 while(true){
//			 try{
//				 
//				 byte[] packetByte  = preParser(inputStream);
//				 
//				// System.out.println(len);
//				// System.out.println(ByteHexUtil.bytesToHexString(Arrays.copyOfRange(recieveBuffer,0,len)));
//				 
//					 
//					 DPacketParser dp = new DPacketParser(packetByte);
//					 switch (dp.pktSignal){
//					 case DPacketParser.SIGNAL_RE_LOGIN:
//						 
//						 Login l = MsgEventHandler.rLogin(dp);	
//						 msg = TrackApp.curHandler.obtainMessage(); 
//						 bundle = new Bundle();
//						 bundle.putParcelable("login", l);
//						 msg.setData(bundle);
//						 msg.what = MSG_LOGIN;
//						 TrackApp.curHandler.sendMessage(msg);
//						
//						 break;
//					 case DPacketParser.SIGNAL_RE_HEARTBEAT:
//						 System.out.println("heart beat");
//						 break;
//					 case DPacketParser.SIGNAL_RE_GETCARGROUP:
//						 CarGroup[] carGroupList = MsgEventHandler.rGetCarGroup(dp);
//						 msg = TrackApp.curHandler.obtainMessage(); 
//						 bundle = new Bundle();
//						 bundle.putParcelableArray("carGroupList", carGroupList);
//						 msg.setData(bundle);
//						 msg.what = MSG_CARGROUPINFO;
//						 TrackApp.curHandler.sendMessage(msg);
//						 break;
//					 case DPacketParser.SIGNAL_RE_GETUSERINFO:
//						 MsgEventHandler.rGetUserInfo(dp);
//						 break;
//					 case DPacketParser.SIGNAL_RE_GETCARINFO:
//						 Car[] carList=MsgEventHandler.rGetCarInfo(dp);
//						 msg = TrackApp.curHandler.obtainMessage(); 
//						 bundle = new Bundle();
//						 bundle.putParcelableArray("carList", carList);
//						 msg.setData(bundle);
//						 msg.what = MSG_CARINFO;
//						 TrackApp.curHandler.sendMessage(msg);
//						 
//						 break;
//					 case DPacketParser.SIGNAL_RE_GETCARTRACK:
//						 Track[] trackList = MsgEventHandler.rGetCarTrack(dp);	
//						 msg = TrackApp.curHandler.obtainMessage(); 
//						 bundle = new Bundle();
//						 bundle.putParcelableArray("trackList", trackList);
//						 msg.setData(bundle);
//						 msg.what = MSG_TRACK;
//						 TrackApp.curHandler.sendMessage(msg);
//						 break;
//					 case DPacketParser.SIGNAL_RE_GETALARMLIST:
//						 Alarm[] alarmList = MsgEventHandler.rGetAlarmList(dp);	
//						 msg = TrackApp.curHandler.obtainMessage(); 
//						 bundle = new Bundle();
//						 bundle.putParcelableArray("alarmlist", alarmList);
//						 msg.setData(bundle);
//						 msg.what = MSG_ALARM;
//						 TrackApp.curHandler.sendMessage(msg);
//						 break;	
//					 case DPacketParser.SIGNAL_FAIL:
//						 MsgEventHandler.rFail(dp);
//						 msg = TrackApp.curHandler.obtainMessage(); 
//						 break;
//					 
//					 }
//				
//				 
//
//			 }catch(Exception e ){
//				e.printStackTrace(); 
//			 }
//			 	  
//		 }
//			
//	 }


	@Override
	public void recivePacket() throws Exception {
		// TODO Auto-generated method stub

		 try{
			 
			 byte[] packetByte  = preParser(inputStream);
			 
			// System.out.println(len);
			// System.out.println(ByteHexUtil.bytesToHexString(Arrays.copyOfRange(recieveBuffer,0,len)));
			 
				 
				 DPacketParser dp = new DPacketParser(packetByte);
				 switch (dp.pktSignal){
				 case DPacketParser.SIGNAL_RE_LOGIN:
					 
					 Login l = MsgEventHandler.rLogin(dp);	
					 msg = TrackApp.curHandler.obtainMessage(); 
					 bundle = new Bundle();
					 bundle.putParcelable("login", l);
					 msg.setData(bundle);
					 msg.what = MSG_LOGIN;
					 TrackApp.curHandler.sendMessage(msg);
					
					 break;
				 case DPacketParser.SIGNAL_RE_HEARTBEAT:
					 System.out.println("heart beat");
					 break;
				 case DPacketParser.SIGNAL_RE_GETCARGROUP:
					 CarGroup[] carGroupList = MsgEventHandler.rGetCarGroup(dp);
					 msg = TrackApp.curHandler.obtainMessage(); 
					 bundle = new Bundle();
					 bundle.putParcelableArray("carGroupList", carGroupList);
					 msg.setData(bundle);
					 msg.what = MSG_CARGROUPINFO;
					 TrackApp.curHandler.sendMessage(msg);
					 break;
				 case DPacketParser.SIGNAL_RE_GETUSERINFO:
					 MsgEventHandler.rGetUserInfo(dp);
					 break;
				 case DPacketParser.SIGNAL_RE_GETCARINFO:
					 Car[] carList=MsgEventHandler.rGetCarInfo(dp);
					 msg = TrackApp.curHandler.obtainMessage(); 
					 bundle = new Bundle();
					 bundle.putParcelableArray("carList", carList);
					 msg.setData(bundle);
					 msg.what = MSG_CARINFO;
					 TrackApp.curHandler.sendMessage(msg);
					 
					 break;
				 case DPacketParser.SIGNAL_RE_GETCARTRACK:
					 Track[] trackList = MsgEventHandler.rGetCarTrack(dp);	
					 msg = TrackApp.curHandler.obtainMessage(); 
					 bundle = new Bundle();
					 bundle.putParcelableArray("trackList", trackList);
					 msg.setData(bundle);
					 msg.what = MSG_TRACK;
					 TrackApp.curHandler.sendMessage(msg);
					 break;
				 case DPacketParser.SIGNAL_RE_GETALARMLIST:
					 Alarm[] alarmList = MsgEventHandler.rGetAlarmList(dp);	
					 msg = TrackApp.curHandler.obtainMessage(); 
					 bundle = new Bundle();
					 bundle.putParcelableArray("alarmlist", alarmList);
					 msg.setData(bundle);
					 msg.what = MSG_ALARM;
					 TrackApp.curHandler.sendMessage(msg);
					 break;	
				 case DPacketParser.SIGNAL_FAIL:
					 MsgEventHandler.rFail(dp);
					 msg = TrackApp.curHandler.obtainMessage(); 
					 break;
				 
				 }
			
			 

		 }catch(Exception e ){
			throw e; 
		 }
		 	  
	 
	}
	

}
