package com.team4.server;

public class Student {
	private String deviceID;
	private String name;
	private Boolean getPlan;
	private Boolean uploadResult;
	
	Student(String NAME, String ID) {
		this.deviceID = ID;
		this.name = NAME;
		this.getPlan = false;
		this.uploadResult = false;
	}
	public String getName(){
		return this.name;
	}
	public String getDeviceID(){
		return this.deviceID;
	}
	public Boolean getGetPlan(){
		return this.getPlan;
	}
	public Boolean getUploadResult(){
		return this.uploadResult;
	}
	
	public void setGetPlan(){
		this.getPlan = true;
	}
	public void setUploadFile() {
		this.uploadResult = true;
	}

}
