package com.cloudbean.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Login implements Parcelable {

	public int isLogin;
	public int userid;
	public String gprsIP;
	public String gprsPort;
	public int userType;
	
	public Login(){
		
	}
	
	public Login(int isLogin, int userid, String gprsIP, String gprsPort,  int userType) {
		super();
		this.isLogin = isLogin;
		this.userid = userid;
		this.gprsIP = gprsIP;
		this.gprsPort = gprsPort;
		this.userType = userType;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeInt(isLogin);
		out.writeInt(userid);
		out.writeString(gprsIP);
		out.writeString(gprsPort);
		out.writeInt(userType);
		
	}
	
	public static final Parcelable.Creator<Login> CREATOR = 
			  new Parcelable.Creator<Login>()
			  { 
			   public Login createFromParcel(Parcel in) 
			   {
				   Login msg = new Login();
				   msg.isLogin = in.readInt();
				   msg.userid = in.readInt();
				   msg.gprsIP = in.readString(); 
				   msg.gprsPort = in.readString(); 
				   msg.userType = in.readInt(); 
				   return msg;
			   }
			   public Login[] newArray(int size) 
			   { 
			    return new Login[size];
			   }
			  };



}
