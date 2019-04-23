package com.team4.server;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication


public class App {
	static ArrayList<String> deviceList = new ArrayList<String>();
	static String classID = "Mobile Computing"; 
	
	public static void main(String[] args) {
    	System.out.println("System Started!!");
    	SpringApplication.run(App.class, args);
	}

	
}
