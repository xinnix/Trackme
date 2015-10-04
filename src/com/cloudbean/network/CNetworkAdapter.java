package com.cloudbean.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.cloudbean.model.Alarm;
import com.cloudbean.model.Car;
import com.cloudbean.model.CarState;
import com.cloudbean.packet.CPacketParser;
import com.cloudbean.packet.DPacketParser;
import com.cloudbean.packet.MsgGPRSParser;
import com.cloudbean.trackerUtil.ByteHexUtil;
import com.cloudbean.trackme.TrackApp;
import com.wilddog.client.Wilddog;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class CNetworkAdapter extends BaseNetworkAdapter {
	
	public static int MSG_FAIL = 0x2000;
	public static int MSG_LOGIN = 0x2001;
	public static int MSG_POSITION = 0x2002;
	public static int MSG_DEF = 0x2003;
	public static int MSG_CIRCUIT = 0x2004;
	public static int MSG_ALARM = 0x2005;
	public static int MSG_POSCOMPLETE = 0x2006;
	
	private Context context =null;
	Map<String, CarState> carPosition=new HashMap<String, CarState>();
	 public CNetworkAdapter(final String serverIP,final int port,Context context){
		super(serverIP,port);
		this.context =context;
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
			final String res;
		 	byte[] packetByte  = preParser();
		 	Message msg = TrackApp.curHandler.obtainMessage();
		 	Bundle b = new Bundle();
			CPacketParser cp = new CPacketParser(packetByte);
			
			 switch (cp.pktSignal){
			 case CPacketParser.SIGNAL_RE_LOGIN:
				 int i =MsgEventHandler.c_rLogin(cp);
				 //msg.what =MSG_LOGIN;
				 break;
			 case CPacketParser.SIGNAL_PREPOSITION:
				 break;
			 case CPacketParser.SIGNAL_POSCOMPLETE:
				 msg.what =MSG_POSCOMPLETE;
				 TrackApp.curHandler.sendMessage(msg);
				 break;
			 case CPacketParser.SIGNAL_CENTERALARM:
				 //final Alarm alarm = null;
				 TrackApp.playAlarmSound();
				 String alarmtype = null;
				 if(cp.pktBuffer[9]==3){
					 if(cp.pktBuffer[19]==1){
						 alarmtype ="进入区域报警";
					 }else{
						 alarmtype = "出区域报警";
					 }
				 }
				 final Alarm alarm = new Alarm(cp.pktFakeIP,alarmtype);
				 TrackApp.alarmList.add(alarm);
				 if(alarm.alarmType != null){
					 new Thread(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Looper.prepare();
								Toast.makeText(context,alarm.termName+"发生"+alarm.alarmType ,Toast.LENGTH_SHORT).show();
								Looper.loop();
							}	 
						 }.start();
				 }
				 
				msg.what =MSG_ALARM;
				TrackApp.curHandler.sendMessage(msg);
				 break;
			 case CPacketParser.SIGNAL_RELAY:
				 MsgGPRSParser mgp =  new MsgGPRSParser(Arrays.copyOfRange(cp.pktData, 4, cp.pktData.length));
				 
				 switch(mgp.msgType){
				 case MsgGPRSParser.MSG_TYPE_DEF:
					 if(TrackApp.curCommand.equals("设防")){
						 res = mgp.msgData.equals("00")?"设防失败":"设防成功";
					 }else{
						 res = mgp.msgData.equals("00")?"撤防失败":"撤防成功";
					 }
					 
					 b.putString("result", res);
					 msg.setData(b);
					 msg.what = MsgGPRSParser.MSG_TYPE_DEF;
					 new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Looper.prepare();
							Toast.makeText(context,res ,Toast.LENGTH_SHORT).show();
							Looper.loop();
						}
						 
					 }.start();
//					 b.putString("devid", mgp.msgTermID);
//					 b.putString("res", mgp.msgData);
//					 msg.what =MSG_DEF;
//					 msg.setData(b);
					 TrackApp.curHandler.sendMessage(msg);
					 break;
				 case MsgGPRSParser.MSG_TYPE_POSITION:
					 CarState cs =MsgEventHandler.c_rGetCarPosition(mgp);
 					 if(cs.gprmc.latitude!=0&&cs.gprmc.longitude!=0){
						 for(int ii=0;ii<TrackApp.carList.length;ii++){
							 if(cs.devid.equals(TrackApp.carList[ii].devId)){
								 TrackApp.carList[ii].setLastState(TrackApp.carList[ii].getCurState());
								 TrackApp.carList[ii].setCurState(cs);
								 TrackApp.carList[ii].alive++;
							 }
						 }
						 
						 carPosition.put(cs.devid, cs);
						 Wilddog devRef = TrackApp.rootRef.child("position");
						 devRef.setValue(carPosition);
						 
						 
						 b.putDouble("lat", cs.gprmc.latitude);
						 b.putDouble("lon", cs.gprmc.longitude);
						 b.putString("speed", cs.gprmc.speed);
						 b.putString("ditant", cs.distant);
						 b.putString("date", cs.gprmc.date);
						 b.putString("devid", cs.devid);
						 b.putString("voltage", cs.analogInput);
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
					
					 if(TrackApp.isLogin==true){
						final Alarm al = MsgEventHandler.c_rGetAlarmInfo(mgp);
						if(al!=null){
							TrackApp.alarmList.add(al);
							TrackApp.playAlarmSound();
							 new Thread(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									Looper.prepare();
									
									Toast.makeText(context, al.termid+al.alarmType+al.alarmTime,Toast.LENGTH_SHORT).show();
									Looper.loop();
								}
								 
							 }.start();
						}
						
					}
					 
					 break;
				 case MsgGPRSParser.MSG_TYPE_PHONE:
						
							res = mgp.msgData.equals("00")?"监听号码设置失败":"监听号码设置成功";
							 new Thread(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									Looper.prepare();
									Toast.makeText(context,res ,Toast.LENGTH_SHORT).show();
									Looper.loop();
								}
								 
							 }.start();
					break;
				 case MsgGPRSParser.MSG_TYPE_GPSREBOOT:
						
						 res = mgp.msgData.equals("00")?"GPS重置失败":"GPS重置成功";
						 new Thread(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Looper.prepare();
								Toast.makeText(context,res ,Toast.LENGTH_SHORT).show();
								Looper.loop();
							}
							 
						 }.start();
					
					 
					 break;
				 case MsgGPRSParser.MSG_TYPE_GPSHEARTBEAT:
						
					 res = mgp.msgData.equals("00")?"GPRS心跳间隔设置失败":"GPRS心跳间隔设置成功";
					 new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Looper.prepare();
							Toast.makeText(context,res ,Toast.LENGTH_SHORT).show();
							Looper.loop();
						}
						 
					 }.start();
				
				 
				 break;
				 
				 case MsgGPRSParser.MSG_TYPE_EXPANDCOMMAND:
						
					 res = mgp.msgData.equals("00")?"扩展命令执行失败":"扩展命令执行成功";
					 new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Looper.prepare();
							Toast.makeText(context,res ,Toast.LENGTH_SHORT).show();
							Looper.loop();
						}
						 
					 }.start();
				
				 
				 break;
				 
				 case MsgGPRSParser.MSG_TYPE_TRACEINTERVAL:
						
					 res = mgp.msgData.equals("00")?"设置定时追踪执行失败":"设置定时追踪执行成功";
					 new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Looper.prepare();
							Toast.makeText(context,res ,Toast.LENGTH_SHORT).show();
							Looper.loop();
						}
						 
					 }.start();
				
				 
				 break;
				 case MsgGPRSParser.MSG_TYPE_SAVEPOWER:
						
					 res = mgp.msgData.equals("00")?"设置休眠失败":"设置休眠成功";
					 new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Looper.prepare();
							Toast.makeText(context,res ,Toast.LENGTH_SHORT).show();
							Looper.loop();
						}
						 
					 }.start();
				
				 
				 break;
				 
				 }
			

		 }

	 }catch(Exception e ){
		throw e; 
		
	 }
	 	  

	}

}
