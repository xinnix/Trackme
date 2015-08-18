package com.cloudbean.network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.cloudbean.model.Car;
import com.cloudbean.model.CarGroup;
import com.cloudbean.model.Login;
import com.cloudbean.model.Track;
import com.cloudbean.packet.DPacketParser;
import com.cloudbean.trackerUtil.ByteHexUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class NetworkAdapter extends Thread {
	public static Socket socket;
	public static OutputStream outputStream;
	public static InputStream inputStream;
	public byte[] sendBuffer;
	public byte[] recieveBuffer = new byte[100000];
	public Handler handler = null;
	
	public Message msg = null;
	public Bundle bundle =null;
	
	
	
	

	public static int MSG_FAIL = 0x1000;
	public static int MSG_LOGIN = 0x1001;
	public static int MSG_CARINFO = 0x1002;
	public static int MSG_CARGROUPINFO = 0x1003;
	public static int MSG_TRACK = 0x1004;
	
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
	 public byte[] preParser(InputStream inputStream){
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
			 e.printStackTrace();
		 }
		 return bos.toByteArray();
	 }
	 public void run(){
		 while(true){
			 try{
				 
				 byte[] packetByte  = preParser(inputStream);
				 
				// System.out.println(len);
				// System.out.println(ByteHexUtil.bytesToHexString(Arrays.copyOfRange(recieveBuffer,0,len)));
				 
					 
					 DPacketParser dp = new DPacketParser(packetByte);
					 switch (dp.pktSignal){
					 case DPacketParser.SIGNAL_RE_LOGIN:
						 
						 Login l = MsgEventHandler.rLogin(dp);	
						 msg = handler.obtainMessage(); 
						 bundle = new Bundle();
						 bundle.putParcelable("login", l);
						 msg.setData(bundle);
						 msg.what = MSG_LOGIN;
						 handler.sendMessage(msg);
						
						 break;
					 case DPacketParser.SIGNAL_RE_HEARTBEAT:
						 System.out.println("heart beat");
						 break;
					 case DPacketParser.SIGNAL_RE_GETCARGROUP:
						 CarGroup[] carGroupList = MsgEventHandler.rGetCarGroup(dp);
						 msg = handler.obtainMessage(); 
						 bundle = new Bundle();
						 bundle.putParcelableArray("carGroupList", carGroupList);
						 msg.setData(bundle);
						 msg.what = MSG_CARGROUPINFO;
						 handler.sendMessage(msg);
						 break;
					 case DPacketParser.SIGNAL_RE_GETUSERINFO:
						 MsgEventHandler.rGetUserInfo(dp);
						 break;
					 case DPacketParser.SIGNAL_RE_GETCARINFO:
						 Car[] carList=MsgEventHandler.rGetCarInfo(dp);
						 msg = handler.obtainMessage(); 
						 bundle = new Bundle();
						 bundle.putParcelableArray("carList", carList);
						 msg.setData(bundle);
						 msg.what = MSG_CARINFO;
						 handler.sendMessage(msg);
						 
						 break;
					 case DPacketParser.SIGNAL_RE_GETCARTRACK:
						 Track[] trackList = MsgEventHandler.rGetCarTrack(dp);	
						 msg = handler.obtainMessage(); 
						 bundle = new Bundle();
						 bundle.putParcelableArray("trackList", trackList);
						 msg.setData(bundle);
						 msg.what = MSG_TRACK;
						 handler.sendMessage(msg);
						 break;	 
					 case DPacketParser.SIGNAL_FAIL:
						 MsgEventHandler.rFail(dp);
						 msg = handler.obtainMessage(); 
						 handler.sendEmptyMessage(MSG_FAIL);
						 break;
					 
					 }
				
				 

			 }catch(Exception e ){
				e.printStackTrace(); 
			 }
			 	  
		 }
			
	 }
	

}
