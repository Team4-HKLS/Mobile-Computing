package com.team4.server;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/register_device")
public class RegistController {
    @PostMapping
	public String registerDevice(@RequestHeader(value="deviceMAC") String deviceID,
							     	@RequestHeader(value="classID") String major,
									@RequestHeader(value="name", defaultValue = "anonymous") String name) {
	    	if(App.classID.contentEquals(major)) {
			Student newStudent = new Student(name, deviceID);
			App.List.add(newStudent);
	        return "200 OK";
    	}else {
            throw new NotExistingException("Not existing classid");
    	}
    }
}
