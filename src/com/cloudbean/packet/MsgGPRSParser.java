package com.cloudbean.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;



public class MsgGPRSParser {
	
	public static short MSG_SEND = 0x01;
	public static short MSG_RECV = 0x02;
	
	
	public String msgHead;
	public short msgLength;
	public String msgTermID;
	public short msgType;
	public String msgData;
	public short msgCheck;
	public String msgEnd;	
	
	public byte[] msgByteBuf;
	 
	
	
	public MsgGPRSParser(byte[] msgByteBuf){
		int head = 0;
		
		this.msgHead = new String(Arrays.copyOfRange(msgByteBuf,head,head+=2));
		this.msgLength = ByteHexUtil.byteToShort(Arrays.copyOfRange(msgByteBuf,head,head+=2));
		this.msgTermID = ByteHexUtil.bytesToHexString(Arrays.copyOfRange(msgByteBuf,head,head+=7));		
		this.msgType = ByteHexUtil.byteToShort(Arrays.copyOfRange(msgByteBuf,head,head+=2));
		int datalen = this.msgLength-2-2-7-2-2-2;
		this.msgData = new String(Arrays.copyOfRange(msgByteBuf,head,head+=datalen));
		this.msgCheck =  ByteHexUtil.byteToShort(Arrays.copyOfRange(msgByteBuf,head,head+=2));
		this.msgEnd = new String(Arrays.copyOfRange(msgByteBuf,head,head+=2));
		
		
		this.msgByteBuf = msgByteBuf;
		
		
	}//end of MsgParser
		
		
		
		
	
	
	
}
