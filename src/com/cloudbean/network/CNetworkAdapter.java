package com.cloudbean.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.cloudbean.model.CarState;
import com.cloudbean.packet.ByteHexUtil;
import com.cloudbean.packet.CPacketParser;
import com.cloudbean.packet.DPacketParser;
import com.cloudbean.trackerUtil.MsgEventHandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class CNetworkAdapter extends Thread {
	
	public static Socket socket;
	public static OutputStream outputStream;
	public static InputStream inputStream;
	public byte[] sendBuffer;
	public byte[] recieveBuffer = new byte[4096];
	
	public Handler handler = null;
	
	public static int MSG_SUCCESS_LOCATE = 0x77;
	
	
	 public CNetworkAdapter(String serverIP,int port){
		 super();
		 try{
			 socket = new Socket(InetAddress.getByName(serverIP),port);
			 outputStream = socket.getOutputStream();
			 inputStream = socket.getInputStream();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
	 }
	 public CNetworkAdapter(byte[] packet){
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
			 short header = dis.readShort();
			 byte signal = dis.readByte();
			 short datalen = dis.readShort();
			 
			 byte[] packet = new byte[datalen];
			 dis.readFully(packet);
			 bos.write(ByteHexUtil.shortToByte(header));
			 bos.write(signal);
			 bos.write(ByteHexUtil.shortToByte(datalen));
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
				
					 CPacketParser cp = new CPacketParser(packetByte);
					 switch (cp.pktSignal){
					 case CPacketParser.SIGNAL_RE_LOGIN:
						 MsgEventHandler.c_rLogin(cp);	
						 break;
					 case CPacketParser.SIGNAL_RE_LOCATE:
						 
						 CarState cs =MsgEventHandler.c_rGetCarPosition(cp);
						 
						 Message msg = handler.obtainMessage();
						 Bundle b = new Bundle();
						 
						 b.putDouble("lat", cs.gprmc.latitude);
						 b.putDouble("lon", cs.gprmc.longitude);
						 b.putString("speed", cs.gprmc.speed);
						 b.putString("ditant", cs.distant);
						 b.putString("date", cs.gprmc.date);
						 b.putString("devid", cs.devid);
						 msg.setData(b);
						 msg.what = MSG_SUCCESS_LOCATE;
						 handler.sendMessage(msg);
						
	
				 }
				 

			 }catch(Exception e ){
				e.printStackTrace(); 
			 }
			 	  
		 }
			
	 }
	


}
