package com.cloudbean.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable{
	
	public String termid;	
	public String alarmTime;
	public String alarmType;
//	public double longitude;
//	public double latitude;
//	public int speed;
//	public int angle;
//	public String address;
	
	public Alarm(String termid, String alarmTime, String alarmType) {
		super();
		this.termid = termid;
		this.alarmTime = alarmTime.trim();
		this.alarmType = alarmType.trim();
//		this.longitude = longitude;
//		this.latitude = latitude;
//		this.speed = speed;
//		this.angle = angle;
//		this.address = address;
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
