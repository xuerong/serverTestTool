package com.io;

import java.io.Serializable;

import com.google.protobuf.AbstractMessage.Builder;
import com.google.protobuf.Message;

public class PBPacket implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int result;
	private int opcode;
	private String session="";
	private byte[] data;
	
	public PBPacket(int opcode, Builder<?> builder) {
		this.opcode = opcode; 
		Message msg = builder.build();
//		log.info("[PBPacket][new] \nopode:{}, message:\n[\n{}]" , opcode,  msg);
		this.data = msg.toByteArray();
	}
	
	public PBPacket(int opcode,byte[] data){
		this.opcode=opcode;
		this.data=data;
	}
	
	public int getOpcode() {
		return opcode;
	}
	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
	
}
