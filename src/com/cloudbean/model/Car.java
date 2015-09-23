package com.cloudbean.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Car  implements Parcelable {
	
	public String id;	
	public String ipAddress;
	public String name;
	public String devId;
	public String devtype;
	public String carGroupId;
	public int alive = 0;
	public CarState lastState;
	public CarState curState;
	public String curAddress;
	
	
	public CarState getCurState() {
		return curState;
	}



	public void setCurState(CarState curState) {
		this.curState = curState;
	}



	public CarState getLastState() {
		return lastState;
	}



	public void setLastState(CarState lastState) {
		this.lastState = lastState;
	}



	public int getAlive() {
		return alive;
	}



	public void setAlive(int alive) {
		this.alive = alive;
	}



	public Car(String id, String ipAddress, String name, String devId, String devtype, String carGroupId) {
		super();
		this.id = id.trim();
		this.ipAddress = ipAddress.trim();
		this.name = name.trim();
		this.devId = devId.trim();
		this.devtype = devtype.trim();
		this.carGroupId = carGroupId.trim();
		this.alive = 0;
		this.lastState = null;
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
		out.writeString(ipAddress);
		out.writeString(name);
		out.writeString(devId);
		out.writeString(devtype);
		out.writeString(carGroupId);
		out.writeInt(alive);
		
		
	}
	
	
	public static final Parcelable.Creator<Car> CREATOR = 
			  new Parcelable.Creator<Car>()
			  { 
			   public Car createFromParcel(Parcel in) 
			   {
				   Car msg = new Car();
				   msg.id = in.readString();
				   msg.ipAddress = in.readString(); 
				   msg.name = in.readString(); 
				   msg.devId = in.readString(); 
				   msg.devtype = in.readString(); 
				   msg.carGroupId = in.readString(); 
				   msg.alive = in.readInt(); 
				   return msg;
			   }
			   public Car[] newArray(int size) 
			   { 
			    return new Car[size];
			   }
			  };

}
