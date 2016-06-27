package com.userhandle;

public class PassSave {
	private byte[] buffer;
	
	public PassSave(){
		buffer=new byte[50];
	}
	public PassSave(String passSaveStr){
		if(passSaveStr!=null){
			buffer=passSaveStr.getBytes();
		}else {
			buffer=new byte[50];
		}
	}
	
	public void setScore(int pass,int score){
		byte b=buffer[pass/4];
		int index=pass%4;
		score<<=(index*2);
		byte t=(byte)(3<<(index*2));
		t^=(byte)255;
		b&=t;
		b|=score;
		buffer[pass/4]=b;
	}
	
	public int getScore(int pass){
		byte b=buffer[pass/4];
		int index=pass%4;
		int t=b>>>(index*2);
		return t&3;
	}
	
	public String toString(){
		return new String(buffer);
	}
}
