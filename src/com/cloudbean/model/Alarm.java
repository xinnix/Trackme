package com.cloudbean.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cloudbean.trackerUtil.IpUtil;
import com.cloudbean.trackme.TrackApp;

import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable{
	public static Map<Integer, String> alarmMap = new HashMap<Integer,String>(){{
		put(0x64,"���İ������������·Ƿ�����");
		put(0x11,"���Ѿ�������ʻ�ˣ��������г���ȫ");
		put(0x12,"��Χ������");
		put(0x50,"GPS��λ���ⲿ��Դ�жϱ���");
		put(0x12,"�ƶ�����");
		put(0x14,"��ӭʹ��GPS��λ��");
		put(0x01,"SOS������ȱ���");
		put(0x10,"����豸��ص�ѹ�ͣ��뼰ʱ���");
		put(0x03,"�Ӵ��ɹ�");
		put(0x33,"���䱨��");
		put(0x66,"��ʱ��ͣ������");
		put(0x67,"�¶��쳣����");
		put(0x68,"���İ����������������𶯱���");
	}};
	
	public String termid;
	public String termName;
	public String alarmTime;
	public String alarmType;
//	public double longitude;
//	public double latitude;
//	public int speed;
//	public int angle;
//	public String address;
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public Alarm(String termid,String alarmType) {
		super();
		this.termid = decodeDevId(termid);
		this.termName = getDevNameById(this.termid);
		this.alarmTime = format.format(new Date());
		this.alarmType = alarmType.trim();
//		this.longitude = longitude;
//		this.latitude = latitude;
//		this.speed = speed;
//		this.angle = angle;
//		this.address = address;
	}
	public Alarm(int carid,String alarmTime,String alarmType) {
		super();
		//this.termid = decodeDevId(termid);
		this.termName = getDevNameByCarid(carid);
		this.alarmTime = alarmTime;
		this.alarmType = alarmType.trim();
//		this.longitude = longitude;
//		this.latitude = latitude;
//		this.speed = speed;
//		this.angle = angle;
//		this.address = address;
	}
	
	public Alarm(int fakeip, String alarmType) {
		super();
		this.termName = getDevNameByFakeip(fakeip);
		this.alarmTime = format.format(new Date());
		this.alarmType = alarmType.trim();
//		this.longitude = longitude;
//		this.latitude = latitude;
//		this.speed = speed;
//		this.angle = angle;
//		this.address = address;
	}
	
	 public String getDevNameById(String devid){
		 String res = null;
		 for (int jj=0;jj<TrackApp.carList.length;jj++){
     		if(devid.equals(TrackApp.carList[jj].devId)){
     			res = TrackApp.carList[jj].name;
     		}
     	}
		 return res;
	 }
	 
	 private String getDevNameByCarid(int carid){
		 String res = null;
		 for (int jj=0;jj<TrackApp.carList.length;jj++){
     		if(carid==Integer.parseInt(TrackApp.carList[jj].id)){
     			res = TrackApp.carList[jj].name;
     		}
     	}
		 return res;
	 }

	 private String getDevNameByFakeip(int fakeip){
		 String res = null;
		 for (int jj=0;jj<TrackApp.carList.length;jj++){
     		if(fakeip==IpUtil.Ip2Int(TrackApp.carList[jj].ipAddress)){
     			res = TrackApp.carList[jj].name;
     		}
     	}
		 return res;
	 }
	
	private String decodeDevId(String devid){
		if(devid.indexOf("f")>0){
			return devid.substring(0,devid.indexOf("f"));
		}
		return devid;
	}
	
	public Alarm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		// TODO Auto-generated method stub
		out.writeString(termid);
		out.writeString(alarmTime);
		out.writeString(alarmType);
//		out.writeDouble(longitude);
//		out.writeDouble(latitude);
//		out.writeInt(speed);
//		out.writeInt(angle);
//		out.writeString(address);
		
	}
	
	
	public static final Parcelable.Creator<Alarm> CREATOR = 
			  new Parcelable.Creator<Alarm>()
			  { 
			   public Alarm createFromParcel(Parcel in) 
			   {
				   Alarm msg = new Alarm();
				   msg.termid = in.readString();
				   msg.alarmTime = in.readString(); 
				   msg.alarmType = in.readString(); 
//				   msg.longitude = in.readDouble(); 
//				   msg.latitude = in.readDouble(); 
//				   msg.speed = in.readInt(); 
//				   msg.angle = in.readInt(); 
//				   msg.address = in.readString(); 
				   return msg;
			   }
			   public Alarm[] newArray(int size) 
			   { 
			    return new Alarm[size];
			   }
			  };
}
