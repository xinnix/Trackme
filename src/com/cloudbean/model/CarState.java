package com.cloudbean.model;

import com.cloudbean.trackerUtil.ByteHexUtil;

public class CarState {
	
	
	
	public class GPRMC {
		public String utc;
		public String locateState;
		public double latitude;
		public String NorS;
		public double longitude;
		public String EorW;
		public String speed;
		public String direction;
		public String date;
		public String declination;
		public String mDirection;
		public String separator;
		public String check;
		
		
		GPRMC(String orgString){
			String org[] = orgString.split(",");
			this.utc = org[0];
			this.locateState = org[1];
			this.latitude = decodeLat(org[2]);
			this.NorS = org[3];
			this.longitude = decodeLon(org[4]);
			this.EorW = org[5];
			this.speed = decodeSpeed(org[6]);
			this.direction = org[7];
			this.date = decodeDate(org[8]);
			this.declination = org[9];
			this.mDirection = org[10];
			this.separator = org[11].substring(0,0);
			this.check = org[11].substring(1, 2);

		}
		
	}
	public String devid;
	
	public GPRMC gprmc;
	public String posAccuracy;
	public String height;
	public String portState;
	public String voltage;
	public String baseStation;
	public String gsmStrength;
	public String distant;
	
	public void setDevid(String devid){
		

		this.devid = devid;
	}
	public String getDevid(){
		return this.devid;
	}
	
	public CarState(String orgString){
		String[] org = orgString.split("\\|");
		this.gprmc = new GPRMC(org[0]);
		this.posAccuracy = org[1];
		this.height	= org[2];
		this.portState	= org[3];
		this.voltage = org[4];
		this.baseStation = org[5];
		this.gsmStrength = org[6];
		this.distant = decodeDistant(org[7]);
		
		
	}
	
	
	private double decodeLat(String lat){
		int a = Integer.parseInt(lat.substring(0, 2));
		double b = Double.parseDouble(lat.substring(2, lat.length()))/60;
		return a+b;
	}
	
	private double decodeLon(String lat){
		int a = Integer.parseInt(lat.substring(0, 3));
		double b = Double.parseDouble(lat.substring(3, lat.length()))/60;
		return a+b;
	}
	
	
	private String decodeDate(String date){
		String y = date.substring(4);
		String m = date.substring(2, 4);
		String d = date.substring(0, 2);
		return y+"��"+m+"��"+d+"��";
	}
	
	private String decodeSpeed(String speed){
		Double v = Double.parseDouble(speed)*1.852;
		return v+"";
	}
	
	private String decodeDistant(String dis){
		return ""+ByteHexUtil.bytesToInt(ByteHexUtil.hexStringToBytes(dis))/1000;
		 
	}
	
	
	
	

}
