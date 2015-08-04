package com.cloudbean.model;

public class Track {
	
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
		this.distant = distant;
		this.status = status;
		this.isLocated = isLocated;
		this.sdate = date;
	}

}
