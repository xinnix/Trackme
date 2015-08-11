package com.cloudbean.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable{
	public static String ACC_START = "ACC¿ª";
	public static String ACC_SHUTDOWN = "ACC¹Ø";
	
	public int carId;
	public double longitude;
	public double latitude;
	public int direction;
	public int speed;
	public boolean alarm;
	public String distant;
	public String status;
	public boolean isLocated;
	public String sdate;
	
	public Track(int carId, double longitude, double latitude, int direction, int speed, boolean alarm, String distant,
			String status, boolean isLocated, String date) {
		super();
		this.carId = carId;
		this.longitude = longitude;
		this.latitude = latitude;
		this.direction = direction;
		this.speed = speed;
		this.alarm = alarm;
		this.distant = distant.trim();
		this.status = status.trim();
		this.isLocated = isLocated;
		this.sdate = date.trim();
	}

	public Track() {
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
		out.writeInt(carId);
		out.writeDouble(longitude);
		out.writeDouble(latitude);
		out.writeInt(direction);
		out.writeInt(speed);
		out.writeByte((byte) (alarm ? 1 : 0));
		out.writeString(distant);
		out.writeString(status);
		out.writeByte((byte) (isLocated ? 1 : 0));
		out.writeString(sdate);
	}
	public static final Parcelable.Creator<Track> CREATOR = 
			  new Parcelable.Creator<Track>()
			  { 
			   public Track createFromParcel(Parcel in) 
			   {
				   Track msg = new Track();
				   msg.carId = in.readInt();
				   msg.longitude = in.readDouble(); 
				   msg.latitude = in.readDouble(); 
				   msg.direction =  in.readInt();
				   msg.speed = in.readInt();
				   msg.alarm = in.readByte() != 0; 
				   msg.distant = in.readString();
				   msg.status = in.readString();
				   msg.isLocated = in.readByte() != 0; 
				   msg.sdate = in.readString();
				   return msg;
			   }
			   public Track[] newArray(int size) 
			   { 
			    return new Track[size];
			   }
			  };


}
