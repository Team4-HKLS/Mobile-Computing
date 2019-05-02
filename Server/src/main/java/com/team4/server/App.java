package com.team4.server;

import java.io.File;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication


public class App {
	static String path = "/home/kwkwon/DeviceFolder";
	static ArrayList<String> deviceList = new ArrayList<String>();
	static String classID = "Mobile Computing"; 
	
	public static void main(String[] args) {
    	System.out.println("System Started!!");
    	File deleteFolder = new File(path);
    	File[] deleteFolderList = deleteFolder.listFiles();
    	for (int i = 0; i < deleteFolderList.length; i++  ) {
    		File[] fileList = deleteFolderList[i].listFiles();
    		for (int j = 0; j < fileList.length; j++  ) {
    			fileList[j].delete();    		
    		}
    		deleteFolderList[i].delete();
    	}
    	
    	SpringApplication.run(App.class, args);
	}

	
}
