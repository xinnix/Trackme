package com.cloudbean.network;

import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public abstract class BaseNetworkAdapter extends Thread{
	private static final int NETWORK_CONNECTED = 0;
	private static final int NETWORK_DISCONNECT = 1;
	
	private int networkState;

	public int getNetworkState() {
		return networkState;
	}

	public void setNetworkState(int networkState) {
		this.networkState = networkState;
	}
	
	public  Socket socket;
	public  OutputStream outputStream;
	public  InputStream inputStream;
	public byte[] sendBuffer;
	public byte[] recieveBuffer = new byte[10000];
	
	
	private String serverIP = null;
	private int port = 0;

	public BaseNetworkAdapter(String serverIP, int port) {
		super();
		this.serverIP = serverIP;
		this.port = port;
	}
	public BaseNetworkAdapter(byte[] packet){
		 super();
		 this.sendBuffer= packet;
	 }
	
	public void connect(){
		 new Thread () {
			 public void run(){
				 while(true){
					 try{
						 try{
							 try{
								 socket = new Socket(InetAddress.getByName(serverIP),port);
								 outputStream = socket.getOutputStream();
								 inputStream = socket.getInputStream();
								 setNetworkState(NETWORK_CONNECTED);
								 while(true){
									 try{
										 recivePacket(); 
									 }catch(EOFException  se){
										 se.printStackTrace();
										 break;
									 }
									 
								 }
							 }catch(SocketTimeoutException  ste){
								 ste.printStackTrace();
								 break;
							 }
							
						 }catch(Exception e){
							 e.printStackTrace();
							 break;
						 } 
						 sleep(100);
					 }catch(Exception e){
						 e.printStackTrace();
					 }
					 
					 
				 }
				
				 
			 }
			
		 }.start();
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
	 
	 
	 public abstract void recivePacket() throws Exception;
	 
//	 public void run(){
//		 while(true){
//			 recivePacket();
//		 }
//	 }
}
