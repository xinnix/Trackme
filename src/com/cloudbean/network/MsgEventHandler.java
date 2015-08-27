package com.cloudbean.network;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import com.cloudbean.model.Alarm;
import com.cloudbean.model.Car;
import com.cloudbean.model.CarGroup;
import com.cloudbean.model.CarState;
import com.cloudbean.model.Fail;
import com.cloudbean.model.Login;
import com.cloudbean.model.Track;
import com.cloudbean.model.User;
import com.cloudbean.packet.CPacketParser;
import com.cloudbean.packet.DPacketParser;
import com.cloudbean.packet.MsgGPRSParser;
import com.cloudbean.trackerUtil.ByteHexUtil;

import android.util.Log;

public class MsgEventHandler {
	public static NetworkAdapter na;
	public static CNetworkAdapter cna;
	public static void config(NetworkAdapter nwa,CNetworkAdapter cwa){
		na = nwa;
		cna = cwa;
	}
	public static void sLogin(String username,String password){
		int[] pktDataColumnType  = {DPacketParser.DATA_TYPE_STRING,DPacketParser.DATA_TYPE_STRING};
		int[] pktDataColumnLength = {username.length()*2,password.length()*2};
		byte[] pktData = new byte[username.length()*2+password.length()*2];
		
		byte[] busername=username.getBytes();
		byte[] bpassword=password.getBytes();
		
		System.arraycopy(busername, 0, pktData, 0, busername.length);
		System.arraycopy(bpassword, 0, pktData, busername.length*2, bpassword.length);
		DPacketParser dp = new DPacketParser(DPacketParser.SIGNAL_LOGIN,1,2,pktDataColumnType, pktDataColumnLength, pktData);	
		na.sendPacket(dp.pktBuffer);
		
	}
	
	public static Login rLogin(DPacketParser dp){
		
		Login l =new Login((Integer) (dp.dataTable.table[0][0]),
				(Integer) (dp.dataTable.table[0][1]),
				(String) (dp.dataTable.table[0][2]),
				(String) (dp.dataTable.table[0][3]),
				(Integer) (dp.dataTable.table[0][4]));
	
		
		return l;
		
	}
	
	
	public static Fail rFail(DPacketParser dp){
		
		Fail f = new Fail((Integer)dp.dataTable.table[0][0],(String)dp.dataTable.table[0][1]);
		
		System.out.print(""+f.signal+'#'+f.reason.trim());
		System.out.println("");	
	
		return f;
		
	}
	
	public static void sHeartBeat(){
		int[] pktDataColumnType  = {DPacketParser.DATA_TYPE_INTEGER};
		int[] pktDataColumnLength = {4};
		byte[] pktData = new byte[4];
		DPacketParser dp = new DPacketParser(DPacketParser.SIGNAL_HEARTBEAT,1,1,pktDataColumnType, pktDataColumnLength, pktData);	
		na.sendPacket(dp.pktBuffer);
		
	}
	
	public static void sGetCarGroup(int userid,String date){
		int[] pktDataColumnType  = {DPacketParser.DATA_TYPE_INTEGER,DPacketParser.DATA_TYPE_STRING};
		int[] pktDataColumnLength = {4,date.length()*2};
		byte[] pktData = new byte[4+date.length()*2];
		
		byte[] buserid = ByteHexUtil.intToByte(userid);
		byte[] bdate = date.getBytes();
		
		System.arraycopy(buserid, 0, pktData, 0, buserid.length);
		System.arraycopy(bdate, 0, pktData, buserid.length, bdate.length*2);
		
		DPacketParser dp = new DPacketParser(DPacketParser.SIGNAL_GETCARGROUP,1,2,pktDataColumnType, pktDataColumnLength, pktData);	
		na.sendPacket(dp.pktBuffer);
		
	}
	
	public static CarGroup[] rGetCarGroup(DPacketParser dp){
		 
		CarGroup[] cg = new CarGroup[dp.dataTable.table.length];
		for (int ii=0;ii<cg.length;ii++){
			cg[ii] = new CarGroup((Integer)dp.dataTable.table[ii][0],
					(String)dp.dataTable.table[ii][1],
					(String)dp.dataTable.table[ii][2],
					(String)dp.dataTable.table[ii][3],
					(String)dp.dataTable.table[ii][4],
					(String)dp.dataTable.table[ii][5],
					(Integer)dp.dataTable.table[ii][6],
					(String)dp.dataTable.table[ii][7]);
		}
		
		
		for (int ii=0;ii<cg.length;ii++){
			System.out.print(""+cg[ii].vehGroupID+'#'+cg[ii].vehGroupName.trim()+'|'+cg[ii].updateTime);
			System.out.println("");	
		}
		
		return cg;
		
	}
	
	public static void sGetUserInfo(int userid){
		int[] pktDataColumnType  = {DPacketParser.DATA_TYPE_INTEGER};
		int[] pktDataColumnLength = {4};
		byte[] pktData = new byte[4];
		
		byte[] buserid = ByteHexUtil.intToByte(userid);
		
		
		System.arraycopy(buserid, 0, pktData, 0, buserid.length);
		
		
		DPacketParser dp = new DPacketParser(DPacketParser.SIGNAL_GETUSERINFO,1,pktDataColumnType.length,pktDataColumnType, pktDataColumnLength, pktData);	
		na.sendPacket(dp.pktBuffer);
		
	}
	
	public static User rGetUserInfo(DPacketParser dp){
		
		User[] u = new User[dp.dataTable.table.length];
		for (int ii=0;ii<u.length;ii++){
			u[ii] = new User((Integer)dp.dataTable.table[ii][0],
					(String)dp.dataTable.table[ii][1],
					(String)dp.dataTable.table[ii][2],
					(Integer)dp.dataTable.table[ii][3],
					(String)dp.dataTable.table[ii][4],
					(String)dp.dataTable.table[ii][5],
					(String)dp.dataTable.table[ii][6],
					(String)dp.dataTable.table[ii][7],
					(String)dp.dataTable.table[ii][8],
					(String)dp.dataTable.table[ii][9],
					(Integer)dp.dataTable.table[ii][10],
					(String)dp.dataTable.table[ii][11]);
		}
		
		
		for (int ii=0;ii<u.length;ii++){
			System.out.print(""+u[ii].username+'#'+u[ii].password.trim()+'|'+u[ii].birthday);
			System.out.println("");	
		}
		
		return u[0];
		
	}
	
	
	public static void sGetCarInfo(int userid,String date){
		int[] pktDataColumnType  = {DPacketParser.DATA_TYPE_INTEGER,DPacketParser.DATA_TYPE_STRING};
		int[] pktDataColumnLength = {4,date.length()*2};
		byte[] pktData = new byte[4+date.length()*2];
		
		byte[] buserid = ByteHexUtil.intToByte(userid);
		byte[] bdate = date.getBytes();
		
		System.arraycopy(buserid, 0, pktData, 0, buserid.length);
		System.arraycopy(bdate, 0, pktData, buserid.length, bdate.length*2);
		
		DPacketParser dp = new DPacketParser(DPacketParser.SIGNAL_GETCARINFO,1,2,pktDataColumnType, pktDataColumnLength, pktData);	
		Log.i("test",ByteHexUtil.bytesToHexString(dp.pktBuffer));
		na.sendPacket(dp.pktBuffer);
		
	}
	
	
	public static Car[] rGetCarInfo(DPacketParser dp){
		 
		Car[] cars = new Car[dp.dataTable.table.length];
		for (int ii=0;ii<cars.length;ii++){
			cars[ii] = new Car((String)dp.dataTable.table[ii][0],
					(String)dp.dataTable.table[ii][3],
					(String)dp.dataTable.table[ii][4],
					(String)dp.dataTable.table[ii][8],
					(String)dp.dataTable.table[ii][14],
					(String)dp.dataTable.table[ii][64]
					);
		}
		
		
//		for (int ii=0;ii<cars.length;ii++){
//			System.out.print(""+cars[ii].id.trim()+'#'+cars[ii].deviceId.trim()+"$"+cars[ii].ipAddress.trim());
//			System.out.println("");	
//		}
		
		return cars;
		
	}
	
	
	public static void sGetCarTrack(int carid,String sdate,String edate){
		int[] pktDataColumnType  = {DPacketParser.DATA_TYPE_INTEGER,DPacketParser.DATA_TYPE_STRING,DPacketParser.DATA_TYPE_STRING};
		int[] pktDataColumnLength = {4,sdate.length()*2,edate.length()*2};
		byte[] pktData = new byte[4+sdate.length()*2+edate.length()*2];
		
		byte[] bcarid =  ByteHexUtil.intToByte(carid);
		byte[] bsdate=sdate.getBytes();
		byte[] bedate=edate.getBytes();
		
		
		System.arraycopy(bcarid, 0, pktData, 0, bcarid.length);
		System.arraycopy(bsdate, 0, pktData, bcarid.length, bsdate.length);
		System.arraycopy(bedate, 0, pktData, bcarid.length+bsdate.length*2, bedate.length);
		DPacketParser dp = new DPacketParser(DPacketParser.SIGNAL_GETCARTRACK,1,3,pktDataColumnType, pktDataColumnLength, pktData);	
		na.sendPacket(dp.pktBuffer);
	}
	public static Track[] rGetCarTrack(DPacketParser dp){
		
		System.out.println("got track info");
		Track[] t = new Track[dp.dataTable.table.length];

			for (int ii=0;ii<t.length;ii++){
				t[ii] = new Track((Integer)dp.dataTable.table[ii][0],
						(Double)dp.dataTable.table[ii][1],
						(Double)dp.dataTable.table[ii][2],
						(Integer)dp.dataTable.table[ii][3],
						(Integer)dp.dataTable.table[ii][4],
						(Boolean)dp.dataTable.table[ii][5],
						(String)dp.dataTable.table[ii][6],
						(String)dp.dataTable.table[ii][7],
						(Boolean)dp.dataTable.table[ii][8],
						(String)dp.dataTable.table[ii][9]
						);
			}
			
			
			for (int ii=0;ii<t.length;ii++){
				System.out.print(""+t[ii].carId+'#'+t[ii].latitude+'|'+t[ii].longitude+"$"+t[ii].sdate);
				System.out.println("");	
			}
		
		return t;
	}
	
	
	
	
	public static void sGetAlarmList(String carid,String startdate,String enddate,String alarmType){
		int[] pktDataColumnType  = {DPacketParser.DATA_TYPE_STRING,DPacketParser.DATA_TYPE_STRING,DPacketParser.DATA_TYPE_STRING,DPacketParser.DATA_TYPE_STRING};
		int[] pktDataColumnLength = {startdate.length()*2,enddate.length()*2,carid.length()*2,alarmType.length()*2};
		byte[] pktData = new byte[startdate.length()*2+enddate.length()*2+carid.length()*2+alarmType.length()*2];
		
		byte[] bstartdate = startdate.getBytes();
		byte[] benddate = enddate.getBytes();
		byte[] bcarid = carid.getBytes();
		byte[] balarmtype = alarmType.getBytes();
		
		System.arraycopy(bstartdate, 0, pktData, 0, bstartdate.length);
		System.arraycopy(benddate, 0, pktData, bstartdate.length*2,benddate.length);
		System.arraycopy(bcarid, 0, pktData,bstartdate.length*2+benddate.length*2 , bcarid.length);
		System.arraycopy(balarmtype, 0, pktData,bstartdate.length*2+benddate.length*2+bcarid.length*2 , balarmtype.length);
		
		
		DPacketParser dp = new DPacketParser(DPacketParser.SIGNAL_GETALARMLIST,1,4,pktDataColumnType, pktDataColumnLength, pktData);
		
		String test =  ByteHexUtil.bytesToHexString(dp.pktBuffer);
		na.sendPacket(dp.pktBuffer);
		
	}
	
	public static Alarm[] rGetAlarmList(DPacketParser dp){
		 
		Alarm[] al = new Alarm[dp.dataTable.table.length];
		for (int ii=0;ii<al.length;ii++){
			al[ii] = new Alarm((Integer)dp.dataTable.table[ii][0],
					(String)dp.dataTable.table[ii][1],
					(String)dp.dataTable.table[ii][2],
					(Double)dp.dataTable.table[ii][3],
					(Double)dp.dataTable.table[ii][4],
					(Integer)dp.dataTable.table[ii][5],
					(Integer)dp.dataTable.table[ii][6],
					(String)dp.dataTable.table[ii][7]);
		}

		return al;
		
	}
	
	
	
	/*
	 * ���������ı�����ؿ��ƺ���
	 */
	
	
	public static void c_sLogin(String username,String password){
		byte signal = (byte)0xa3;
		int fakeip = 0;
		byte[] busername = username.getBytes();
		byte[] bpassword = password.getBytes();
		byte[] data = new byte[40];
		for (int ii=0;ii<data.length;ii++){
			data[ii]=(byte)0x20;
		}
		
		System.arraycopy(busername, 0, data, 0, busername.length);
		System.arraycopy(bpassword, 0, data, 20, bpassword.length);
		
		CPacketParser cp = new CPacketParser(signal,fakeip, data);
		System.out.println(ByteHexUtil.bytesToHexString(cp.pktBuffer));
		cna.sendPacket(cp.pktBuffer);
			
	}
	
	public static int c_rLogin(CPacketParser cp){
		
		byte sig = ByteHexUtil.intToByte(cp.pktFakeIP)[0];
		
		if (sig==(byte)0x01){
			System.out.println("login complete");
			//c_sGetAllCarPosition();
			return 0;
		}else{
			return 1;
		}
	
	}
	
	
	public static void c_sGetAllLastPosition(){
		String hexPacket  = "2929a4000600000000";
		String end = "0d";
		byte[] packet = ByteHexUtil.hexStringToBytes(hexPacket);
		byte check = CPacketParser.packetCheck(packet);
		ByteArrayOutputStream  bis = new ByteArrayOutputStream();
		try{
			bis.write(packet);
			bis.write(check);
			bis.write(ByteHexUtil.hexStringToBytes(end));
		
			
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(ByteHexUtil.bytesToHexString(bis.toByteArray()));
		
		cna.sendPacket(bis.toByteArray());
		
			
	}
	
	
	public static CarState c_rGetAllCarPosition(CPacketParser cp){
		MsgGPRSParser mgp =  new MsgGPRSParser(Arrays.copyOfRange(cp.pktData, 4, cp.pktData.length));
		CarState cs = new CarState(mgp.msgData);
		return cs;
		
	}
	
	
	public static void c_sGetCarPosition(Car car){
		
		c_sCommand(car,MsgGPRSParser.MSG_TYPE_GETPOSITION,"");
			
	}
	
	
	public static CarState c_rGetCarPosition(MsgGPRSParser mgp){

		CarState cs = new CarState(mgp.msgData);
		int i  = mgp.msgTermID.indexOf("f");	
		cs.setDevid(mgp.msgTermID.substring(0, i));
		return cs;
		
			
	}
	
	
	
	
	
	
	
	public static void c_sSetDef(Car car,String data){
		
		c_sCommand(car,MsgGPRSParser.MSG_TYPE_DEF,data);
			
	}
	public static void c_sSetCircuit(Car car,String data){	
		c_sCommand(car,MsgGPRSParser.MSG_TYPE_CIRCUIT,data);
	}
	
	public static void c_sCommand(Car car,short commandType,String data){
		String devid = car.devId;
		for(int i = (14-devid.length());i>0;i--){
			devid=devid.concat("f");
		}
		int fakeip = ByteHexUtil.bytesToInt(ipToBytesByReg(car.ipAddress));
		ByteArrayOutputStream  bis = new ByteArrayOutputStream();
		bis.write(0x0b);
		MsgGPRSParser mgp = new MsgGPRSParser(devid, commandType, data);
		try{
			bis.write(mgp.msgByteBuf);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		CPacketParser cp = new CPacketParser(CPacketParser.SIGNAL_RELAY, fakeip, bis.toByteArray());
		String test = ByteHexUtil.bytesToHexString(cp.pktBuffer);
		
		cna.sendPacket(cp.pktBuffer);
	}
	
	
	/**
     * ��IP��ַת��Ϊint
     * @param ipAddr
     * @return int
     */
    private static byte[] ipToBytesByReg(String ipAddr) {
        byte[] ret = new byte[4];
        try {
            String[] ipArr = ipAddr.split("\\.");
            ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
            ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
            ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
            ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);
            return ret;
        } catch (Exception e) {
            throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }

    }

}
