package com.cloudbean.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CarGroup implements Parcelable{
	
	public int vehGroupID;
	public String vehGroupName;
	public String contact;
	public String sTel1;
	public String sTel2;
	public String address;
	public int fVehGroupID;
	public String updateTime;
	
	public CarGroup(int vehGroupID, String vehGroupName, String contact, String sTel1, String sTel2, String address,
			int fVehGroupID, String updateTime) {
		super();
		this.vehGroupID = vehGroupID;
		this.vehGroupName = vehGroupName;
		this.contact = contact;
		this.sTel1 = sTel1;
		this.sTel2 = sTel2;
		this.address = address;
		this.fVehGroupID = fVehGroupID;
		this.updateTime = updateTime;
	}

	public CarGroup() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		// TODO Auto-generated method stub
		out.writeInt(vehGroupID);
		out.writeString(vehGroupName);
		out.writeString(contact);
		out.writeString(sTel1);
		out.writeString(sTel2);
		out.writeString(address);
		out.writeInt(fVehGroupID);
		out.writeString(updateTime);
		
	}
	
	public static final Parcelable.Creator<CarGroup> CREATOR = 
			  new Parcelable.Creator<CarGroup>()
			  { 
			   public CarGroup createFromParcel(Parcel in) 
			   {
				   CarGroup msg = new CarGroup();
				   msg.vehGroupID = in.readInt();
				   msg.vehGroupName = in.readString(); 
				   msg.contact = in.readString(); 
				   msg.sTel1 =  in.readString();
				   msg.sTel2 = in.readString();
				   msg.address = in.readString(); 
				   msg.fVehGroupID = in.readInt();
				   msg.updateTime = in.readString();
				  
				   return msg;
			   }
			   public CarGroup[] newArray(int size) 
			   { 
			    return new CarGroup[size];
			   }
			  };
}
