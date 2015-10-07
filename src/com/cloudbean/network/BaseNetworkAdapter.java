package com.cloudbean.network;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.cloudbean.trackme.TrackApp;
import com.wilddog.client.Wilddog;

public abstract class BaseNetworkAdapter extends Thread{
	public static final int NETWORK_CONNECTED = 0x1111;
	public static final int NETWORK_DISCONNECT = 0x1112;
//	public static Wilddog ref = new Wilddog("https://trace.wilddogio.com/"); 
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
	public Thread reconnThread;
	
	public  DataInputStream dis;
	 
	 
	public byte[] sendBuffer;
	public byte[] recieveBuffer = new byte[20000];
	
	
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
		reconnThread = new Thread () {
			public void run(){
				try{
					while(!isInterrupted()){
						
						try{
							socket = new Socket(InetAddress.getByName(serverIP),port);
							outputStream = socket.getOutputStream();
							inputStream = socket.getInputStream();
							dis =  new DataInputStream((new BufferedInputStream(inputStream)));
							setNetworkState(NETWORK_CONNECTED);
							if(TrackApp.curUsername!=null){
								MsgEventHandler.sLogin(TrackApp.curUsername, TrackApp.curPassword);
								MsgEventHandler.c_sLogin(TrackApp.curUsername, TrackApp.curPassword);
							}
							TrackApp.curHandler.sendEmptyMessage(NETWORK_CONNECTED);
							while(true){
								try{
									recivePacket(); 
								}catch(EOFException  e){
									e.printStackTrace();
									break;
								}
							}
						}catch (InterruptedException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
							break;
						}catch(Exception e ){
							e.printStackTrace();
						}// end of try		 
						
						sleep(1000);
						
				}// end of while
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	};
		reconnThread.start();
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
