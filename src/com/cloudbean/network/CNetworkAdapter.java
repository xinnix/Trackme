package com.cloudbean.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.cloudbean.model.Alarm;
import com.cloudbean.model.Car;
import com.cloudbean.model.CarState;
import com.cloudbean.packet.CPacketParser;
import com.cloudbean.packet.DPacketParser;
import com.cloudbean.packet.MsgGPRSParser;
import com.cloudbean.trackerUtil.ByteHexUtil;
import com.cloudbean.trackme.TrackApp;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class CNetworkAdapter extends BaseNetworkAdapter {
	
	public static int MSG_FAIL = 0x2000;
	public static int MSG_LOGIN = 0x2001;
	public static int MSG_POSITION = 0x2002;
	public static int MSG_DEF = 0x2003;
	public static int MSG_CIRCUIT = 0x2004;
	public static int MSG_ALARM = 0x2005;
	
	private Application app =null;
	
	 public CNetworkAdapter(final String serverIP,final int port,Application app){
		super(serverIP,port);
		this.app =app;
		connect(); 
	 }

	 public CNetworkAdapter(byte[] packet){
		 super(packet);
	 }
	 
//	 public void setHandler(Handler hd){
//		 handler = hd;
//	 }
//	 
	 
	 public byte[] preParser(){
		 
		 ByteArrayOutputStream  bos = new ByteArrayOutputStream();
		 try{
			 short header = dis.readShort();
			 byte signal = dis.readByte();
			 short datalen = dis.readShort();
			 if(signal == 0xa9){
				 System.out.print("pos complete");
			 }
			 byte[] packet = new byte[datalen];
			 dis.readFully(packet);
			 bos.write(ByteHexUtil.shortToByte(header));
			 bos.write(signal);
			 bos.write(ByteHexUtil.shortToByte(datalen));
			 bos.write(packet);
			 
			 
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 byte[] buf = bos.toByteArray();
		 String res = ByteHexUtil.bytesToHexString(buf);
		 return buf;
	 }
	 
//	 public void run(){
//		 while(true){
//			 try{		  			 			 
//				 	byte[] packetByte  = preParser(inputStream);
//				 	Message msg = TrackApp.curHandler.obtainMessage();
//				 	Bundle b = new Bundle();
//					CPacketParser cp = new CPacketParser(packetByte);
//					 switch (cp.pktSignal){
//					 case CPacketParser.SIGNAL_RE_LOGIN:
//						 MsgEventHandler.c_rLogin(cp);
//						 msg.what =MSG_LOGIN;
//						 break;
//					 case CPacketParser.SIGNAL_RELAY:
//						 MsgGPRSParser mgp =  new MsgGPRSParser(Arrays.copyOfRange(cp.pktData, 4, cp.pktData.length));
//						 
//						 switch(mgp.msgType){
//						 case MsgGPRSParser.MSG_TYPE_DEF:
//							
//							 b.putString("devid", mgp.msgTermID);
//							 b.putString("res", mgp.msgData);
//							 msg.what =MSG_DEF;
//							 msg.setData(b);
//							 TrackApp.curHandler.sendMessage(msg);
//							 break;
//						 case MsgGPRSParser.MSG_TYPE_POSITION:
//							 CarState cs =MsgEventHandler.c_rGetCarPosition(mgp);
//							 if(cs.gprmc.latitude!=0&&cs.gprmc.longitude!=0){
//								 b.putDouble("lat", cs.gprmc.latitude);
//								 b.putDouble("lon", cs.gprmc.longitude);
//								 b.putString("speed", cs.gprmc.speed);
//								 b.putString("ditant", cs.distant);
//								 b.putString("date", cs.gprmc.date);
//								 b.putString("devid", cs.devid);
//								 b.putString("voltage", cs.voltage);
//								 b.putString("gsmStrength", cs.gsmStrength);
//								
//								 msg.setData(b);
//								 msg.what = MSG_POSITION;
//								 TrackApp.curHandler.sendMessage(msg);
//							 }
//							 break;
//						 case MsgGPRSParser.MSG_TYPE_CIRCUIT:
//							 String test = ByteHexUtil.bytesToHexString(mgp.msgByteBuf);
//							 b.putString("devid", mgp.msgTermID);
//							 b.putString("res", mgp.msgData);
//							 msg.what =MSG_CIRCUIT;
//							 msg.setData(b);
//							 TrackApp.curHandler.sendMessage(msg);
//							 break;
//						 }
//						
//						 break;
//					
//						
//	
//				 }
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
		 	byte[] packetByte  = preParser();
		 	Message msg = TrackApp.curHandler.obtainMessage();
		 	Bundle b = new Bundle();
			CPacketParser cp = new CPacketParser(packetByte);
			
			 switch (cp.pktSignal){
			 case CPacketParser.SIGNAL_RE_LOGIN:
				 int i =MsgEventHandler.c_rLogin(cp);
				 msg.what =MSG_LOGIN;
				 break;
			 case CPacketParser.SIGNAL_PREPOSITION:
				 System.out.println("pre pos");
				 break;
			 case CPacketParser.SIGNAL_POSCOMPLETE:
				 
				 break;
			 case CPacketParser.SIGNAL_RELAY:
				 MsgGPRSParser mgp =  new MsgGPRSParser(Arrays.copyOfRange(cp.pktData, 4, cp.pktData.length));
				 
				 switch(mgp.msgType){
				 case MsgGPRSParser.MSG_TYPE_DEF:
					
					 b.putString("devid", mgp.msgTermID);
					 b.putString("res", mgp.msgData);
					 msg.what =MSG_DEF;
					 msg.setData(b);
					 TrackApp.curHandler.sendMessage(msg);
					 break;
				 case MsgGPRSParser.MSG_TYPE_POSITION:
					 CarState cs =MsgEventHandler.c_rGetCarPosition(mgp);
					 if(cs.gprmc.latitude!=0&&cs.gprmc.longitude!=0){
						 for(int ii=0;ii<TrackApp.carList.length;ii++){
							 if(cs.devid.equals(TrackApp.carList[ii].devId)){
								 TrackApp.carList[ii].setLastState(cs);
								 TrackApp.carList[ii].alive++;
							 }
						 }
						 
						 b.putDouble("lat", cs.gprmc.latitude);
						 b.putDouble("lon", cs.gprmc.longitude);
						 b.putString("speed", cs.gprmc.speed);
						 b.putString("ditant", cs.distant);
						 b.putString("date", cs.gprmc.date);
						 b.putString("devid", cs.devid);
						 b.putString("voltage", cs.voltage);
						 b.putString("gsmStrength", cs.gsmStrength);
						
						 msg.setData(b);
						 msg.what = MSG_POSITION;
						 TrackApp.curHandler.sendMessage(msg);
					 }
					 break;
				 case MsgGPRSParser.MSG_TYPE_CIRCUIT:
					 String test = ByteHexUtil.bytesToHexString(mgp.msgByteBuf);
					 b.putString("devid", mgp.msgTermID);
					 b.putString("res", mgp.msgData);
					 msg.what =MSG_CIRCUIT;
					 msg.setData(b);
					 TrackApp.curHandler.sendMessage(msg);
					 break;
				 case MsgGPRSParser.MSG_TYPE_ALARM:
					
					 Alarm al = MsgEventHandler.c_rGetAlarmInfo(mgp);
					 TrackApp.alarmList.add(al);
					 
					 break;
				 }
			

		 }

	 }catch(Exception e ){
		throw e; 
		
	 }
	 	  

	}

}
