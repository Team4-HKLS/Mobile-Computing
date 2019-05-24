package com.team4.server;

import com.team4.server.App.State;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/register_device")
public class RegistController {
	@PostMapping
	public String registerDevice(@RequestHeader(value = "deviceMAC") String deviceID,
			@RequestHeader(value = "classID") String major,
			@RequestHeader(value = "name", defaultValue = "anonymous") String name) {
<<<<<<< HEAD
		if (App.state != State.Register)
			throw new NotAllowedException("Register device is not allowed in this time");

=======
>>>>>>> 8b035a0514dc41a721f7e548fbed8df8c1b001c0
		if (App.classID.contentEquals(major)) {
			if (App.searchDevice(deviceID) == -1) {
				Student newStudent = new Student(name, deviceID);
				App.List.add(newStudent);
				return "Success!!";
			}
			return "Success!!";
		} else {
			throw new NotExistingException("Not existing classid");
		}
	}
}
