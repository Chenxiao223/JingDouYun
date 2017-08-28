package com.dao;

public class ShowInfo {

	
	
	String Epc;
	String time;
	boolean UploadFlag;
	
	public boolean getUploadFlag() {
		return UploadFlag;
	}
	public void setUploadFlag(boolean uploadFlag) {
		UploadFlag = uploadFlag;
	}
	public String getEpc() {
		return Epc;
	}
	public void setEpc(String Epc) {
		this.Epc = Epc;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
}
