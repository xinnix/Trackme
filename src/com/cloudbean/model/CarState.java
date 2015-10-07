package com.cloudbean.model;

import java.text.DecimalFormat;

import com.cloudbean.trackerUtil.ByteHexUtil;
import com.cloudbean.trackme.TrackApp;

public class CarState {
	
	
	
	public GPRMC getGprmc() {
		return gprmc;
	}
	public void setGprmc(GPRMC gprmc) {
		this.gprmc = gprmc;
	}
	public String getPosAccuracy() {
		return posAccuracy;
	}
	public void setPosAccuracy(String posAccuracy) {
		this.posAccuracy = posAccuracy;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public byte[] getPortState() {
		return portState;
	}
	public void setPortState(byte[] portState) {
		this.portState = portState;
	}
	public String getAnalogInput() {
		return analogInput;
	}
	public void setAnalogInput(String analogInput) {
		this.analogInput = analogInput;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getBaseStation() {
		return baseStation;
	}
	public void setBaseStation(String baseStation) {
		this.baseStation = baseStation;
	}
	public String getGsmStrength() {
		return gsmStrength;
	}
	public void setGsmStrength(String gsmStrength) {
		this.gsmStrength = gsmStrength;
	}
	public String getDistant() {
		return distant;
	}
	public void setDistant(String distant) {
		this.distant = distant;
	}
	public CarState() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CarState(String devid, GPRMC gprmc, String posAccuracy, String height, byte[] portState, String analogInput,
			String temperature, String baseStation, String gsmStrength, String distant, String voltage) {
		super();
		this.devid = devid;
		this.gprmc = gprmc;
		this.posAccuracy = posAccuracy;
		this.height = height;
		this.portState = portState;
		this.analogInput = analogInput;
		this.temperature = temperature;
		this.baseStation = baseStation;
		this.gsmStrength = gsmStrength;
		this.distant = distant;
		this.voltage = voltage;
	}
	public String getVoltage() {
		return voltage;
	}
	public void setVoltage(String voltage) {
		this.voltage = voltage;
	}

	public class GPRMC {
		public String utc;
		public String locateState;
		public double latitude;
		public String NorS;
		public double longitude;
		public String EorW;
		public String speed;
		public String direction;
		public GPRMC() {
			super();
			// TODO Auto-generated constructor stub
		}


		public GPRMC(String utc, String locateState, double latitude, String norS, double longitude, String eorW,
				String speed, String direction, String date, String declination, String mDirection, String separator,
				String check) {
			super();
			this.utc = utc;
			this.locateState = locateState;
			this.latitude = latitude;
			NorS = norS;
			this.longitude = longitude;
			EorW = eorW;
			this.speed = speed;
			this.direction = direction;
			this.date = date;
			this.declination = declination;
			this.mDirection = mDirection;
			this.separator = separator;
			this.check = check;
		}


		public String getUtc() {
			return utc;
		}


		public void setUtc(String utc) {
			this.utc = utc;
		}


		public String getLocateState() {
			return locateState;
		}


		public void setLocateState(String locateState) {
			this.locateState = locateState;
		}


		public double getLatitude() {
			return latitude;
		}


		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}


		public String getNorS() {
			return NorS;
		}


		public void setNorS(String norS) {
			NorS = norS;
		}


		public double getLongitude() {
			return longitude;
		}


		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}


		public String getEorW() {
			return EorW;
		}


		public void setEorW(String eorW) {
			EorW = eorW;
		}


		public String getSpeed() {
			return speed;
		}


		public void setSpeed(String speed) {
			this.speed = speed;
		}


		public String getDirection() {
			return direction;
		}


		public void setDirection(String direction) {
			this.direction = direction;
		}


		public String getDate() {
			return date;
		}


		public void setDate(String date) {
			this.date = date;
		}


		public String getDeclination() {
			return declination;
		}


		public void setDeclination(String declination) {
			this.declination = declination;
		}


		public String getmDirection() {
			return mDirection;
		}


		public void setmDirection(String mDirection) {
			this.mDirection = mDirection;
		}


		public String getSeparator() {
			return separator;
		}


		public void setSeparator(String separator) {
			this.separator = separator;
		}


		public String getCheck() {
			return check;
		}


		public void setCheck(String check) {
			this.check = check;
		}


		public String date;
		public String declination;
		public String mDirection;
		public String separator;
		public String check;
		
		
		GPRMC(String orgString) throws Exception{
			String org[] = orgString.split(",");
			if(org.length<12){
				throw new Exception("gprmc struct error");
			}
			this.utc = org[0];
			this.locateState = org[1];
			if(this.locateState.equals("A")){
				this.latitude = decodeLat(org[2]);
				this.NorS = org[3];
				this.longitude = decodeLon(org[4]);
				this.EorW = org[5];
				this.speed = decodeSpeed(org[6]);
			}else{
				this.latitude = 0;
				this.NorS = "";
				this.longitude = 0;
				this.EorW = "";
				this.speed = "0";
			}
			
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
	public byte[] portState;
	public String analogInput;
	public String temperature;
	public String baseStation;
	public String gsmStrength;
	public String distant;
	public String voltage;
	
	public void setDevid(String devid){
		

		this.devid = devid;
	}
	public String getDevid(){
		return this.devid;
	}
	
	public CarState(String orgString) throws Exception{
		String[] org = orgString.split("\\|");
		
		this.gprmc = new GPRMC(org[0]);
		
		
		this.posAccuracy = org[1];
		this.height	= org[2];
		this.portState	= ByteHexUtil.hexStringToBytes(org[3].trim());
		this.analogInput  = org[4];
		this.temperature = ""+decodeTemp(this.analogInput);
		this.baseStation = org[5];
		this.gsmStrength = org[6];
		this.distant = decodeDistant(org[7]);
		this.voltage =decodeVoltage(org[8]);
		
	}
	
	
	private double decodeLat(String lat){
		int a = Integer.parseInt(lat.substring(0, 2));
		double b = Double.parseDouble(lat.substring(2, lat.length()))/60;
		return a+b;
	}
	
	private double decodeTemp(String analogInput){
		String[] tmp =  analogInput.split(",");
		String res;
		if (tmp[1].length()<3){
			res = "00"+tmp[1].substring(tmp[1].length()-2);
		}else{
			res = tmp[1];
		}
		short ress = ByteHexUtil.byteToShort(ByteHexUtil.hexStringToBytes(res));
		return ress;
	}
	private double decodeLon(String lat){
		int a = Integer.parseInt(lat.substring(0, 3));
		double b = Double.parseDouble(lat.substring(3, lat.length()))/60;
		return a+b;
	}
	private String decodeVoltage(String voltage){
		byte[] voltageByte = ByteHexUtil.hexStringToBytes(voltage); 
		int voltageInt  = ByteHexUtil.byteToShort(voltageByte);
		double res = (voltageInt*3.2*16)/4096;
		DecimalFormat formatter = new DecimalFormat("##0.0");
		
		return formatter.format(res);
		
	}
	
	private String decodeDate(String date){
		String y = date.substring(4);
		String m = date.substring(2, 4);
		String d = date.substring(0, 2);
		return y+"年"+m+"月"+d+"日";
	}
	
	private String decodeSpeed(String speed){
		Double v = Double.parseDouble(speed)*1.852;
		v = v<6?0:v;//速度小于6km过滤
		DecimalFormat formatter = new DecimalFormat("##0.00");
		String ns = formatter.format(v);
		
		return ns;
	}
	
	private String decodeDistant(String dis){
		return ""+ByteHexUtil.bytesToInt(ByteHexUtil.hexStringToBytes(dis))/1000;
		 
	}
	
	
	
	

}
