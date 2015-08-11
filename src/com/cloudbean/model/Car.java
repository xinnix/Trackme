package com.cloudbean.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Car  implements Parcelable {
	
	public String id;
	public String deviceId;
	public String ipAddress;
	public String cph;
	public String deCph;
	
	public Car(String id, String deviceId, String ipAddress,String cph,String deCph) {
		super();
		this.id = id.trim();
		this.deviceId = deviceId.trim();
		this.ipAddress = ipAddress.trim();
		this.cph =cph.trim();
		this.deCph = deCph.trim();
	}

	public Car() {
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
		out.writeString(id);
		out.writeString(deviceId);
		out.writeString(ipAddress);
		out.writeString(cph);
		out.writeString(deCph);
		
	}
	
	
	public static final Parcelable.Creator<Car> CREATOR = 
			  new Parcelable.Creator<Car>()
			  { 
			   public Car createFromParcel(Parcel in) 
			   {
				   Car msg = new Car();
				   msg.id = in.readString();
				   msg.deviceId = in.readString(); 
				   msg.ipAddress = in.readString(); 
				   msg.cph = in.readString(); 
				   msg.deCph = in.readString(); 

				   return msg;
			   }
			   public Car[] newArray(int size) 
			   { 
			    return new Car[size];
			   }
			  };

}
