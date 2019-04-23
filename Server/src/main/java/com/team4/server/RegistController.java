package com.team4.server;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/register_device")
public class RegistController {
    @PostMapping
    public String registerDevice(@RequestHeader(value="deviceUUID") String deviceID,@RequestHeader(value="major") String major/*@RequestParam("deviceUUID") String deviceID, @RequestParam("major") String major*/) {
    	

	    	if(App.classID.contentEquals(major)) {
	    	App.deviceList.add(deviceID);
	    	System.out.println("Devicelist :: "+App.deviceList);
	        return "200 OK";
    	}else {
            throw new NotExistingClassException("Not existing classid");
    	}
    }
}