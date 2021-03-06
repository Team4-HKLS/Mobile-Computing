package com.team4.server;

public class Student {
	private String deviceID;
	private String name;
	private Boolean getPlan;
	private Boolean uploadResult;
	private Boolean clusteringResult;
	private Boolean finalResult;
	
	Student(String NAME, String ID) {
		this.deviceID = ID;
		this.name = NAME;
		this.getPlan = false;
		this.uploadResult = false;
		this.clusteringResult = false;
		this.finalResult = false;
		if(NAME.contentEquals("GOD")){
			this.getPlan = true;
			this.uploadResult = true;
			this.clusteringResult = true;
			this.finalResult = true;
		}
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
	public Boolean getClusteringResult(){
		return this.clusteringResult;
	}
	public Boolean getFinalResult(){
		return this.finalResult;
	}
	
	public void setGetPlan(){
		this.getPlan = true;
	}
	public void setUploadFile() {
		this.uploadResult = true;
	}
	public void setClusteringResult(boolean b) {
		this.clusteringResult = b;
	}

	public void setFinalResult(){
		this.finalResult = true;
	}
}
