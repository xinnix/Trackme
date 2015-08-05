package com.cloudbean.network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.cloudbean.model.Login;
import com.cloudbean.packet.ByteHexUtil;
import com.cloudbean.packet.DPacketParser;
import com.cloudbean.trackerUtil.MsgEventHandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class NetworkAdapter extends Thread {
	public static Socket socket;
	public static OutputStream outputStream;
	public static InputStream inputStream;
	public byte[] sendBuffer;
	public byte[] recieveBuffer = new byte[4096];
	public Handler handler = null;
	
	 public NetworkAdapter(String serverIP,int port){
		 super();
		 try{
			 socket = new Socket(InetAddress.getByName(serverIP),port);
			 outputStream = socket.getOutputStream();
			 inputStream = socket.getInputStream();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
	 }
	 public NetworkAdapter(byte[] packet){
		 super();
		 this.sendBuffer= packet;
	 }
	 
	 
	 public void sendPacket(byte[] packet){
		 try{
			 this.sendBuffer = packet;
			 outputStream.write(packet);
			 outputStream.flush();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
	 }
	 
	 public void setHandler(Handler hd){
		 handler = hd;
	 }
	 
	 public void run(){
		 while(true){
			 try{		  			 			 
				 int len = inputStream.read(recieveBuffer);
				// System.out.println(len);
				// System.out.println(ByteHexUtil.bytesToHexString(Arrays.copyOfRange(recieveBuffer,0,len)));
				 if(len>0){
					 
					 DPacketParser dp = new DPacketParser(Arrays.copyOfRange(recieveBuffer,0,len));
					 switch (dp.pktSignal){
					 case DPacketParser.SIGNAL_RE_LOGIN:
						 
						 Login l = MsgEventHandler.rLogin(dp);
						 Thread.sleep(3000);
						 Message msg = handler.obtainMessage(); 
						 Bundle b = new Bundle();
						 b.putParcelable("login", l);
						 msg.setData(b);
						 handler.sendMessage(msg);
						
						 break;
					 case DPacketParser.SIGNAL_RE_HEARTBEAT:
						 System.out.println("heart beat");
						 break;
					 case DPacketParser.SIGNAL_RE_GETUSERCARGROUP:
						 MsgEventHandler.rGetCarGroup(dp);
						 break;
					 case DPacketParser.SIGNAL_RE_GETUSERINFO:
						 MsgEventHandler.rGetUserInfo(dp);
						 break;
					 case DPacketParser.SIGNAL_RE_GETCARINFO:
						 MsgEventHandler.rGetCarInfo(dp);
						 break;
					 case DPacketParser.SIGNAL_RE_GETCARTRACK:
						 MsgEventHandler.rGetCarTrack(dp);
						 break;	 
					 case DPacketParser.SIGNAL_FAIL:
						 MsgEventHandler.rFail(dp);
						 break;
					 
					 }
				
				 
				 }
				 

			 }catch(Exception e ){
				e.printStackTrace(); 
			 }
			 	  
		 }
			
	 }
	

}
