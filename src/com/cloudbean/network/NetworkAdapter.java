package com.cloudbean.network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.cloudbean.model.Car;
import com.cloudbean.model.Login;
import com.cloudbean.model.Track;
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
	public byte[] recieveBuffer = new byte[100000];
	public Handler handler = null;
	
	public Message msg = null;
	public Bundle bundle =null;
	
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
						 Car[] carList=MsgEventHandler.rGetCarInfo(dp);						
						
						 msg = handler.obtainMessage(); 
						 bundle = new Bundle();
						 bundle.putParcelableArray("carList", carList);
						 msg.setData(bundle);
						 handler.sendMessage(msg);
						 break;
					 case DPacketParser.SIGNAL_RE_GETCARTRACK:
						 Track[] trackList = MsgEventHandler.rGetCarTrack(dp);	
						 msg = handler.obtainMessage(); 
						 bundle = new Bundle();
						 bundle.putParcelableArray("trackList", trackList);
						 msg.setData(bundle);
						 handler.sendMessage(msg);
						 break;	 
					 case DPacketParser.SIGNAL_FAIL:
						 MsgEventHandler.rFail(dp);
						 break;
					 
					 }
				
				 

			 }catch(Exception e ){
				e.printStackTrace(); 
			 }
			 	  
		 }
			
	 }
	

}
