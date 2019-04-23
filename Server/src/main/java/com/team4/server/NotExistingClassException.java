package com.team4.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Not existing classid")
public class NotExistingClassException extends RuntimeException  {
	
	public NotExistingClassException(String message) {
		super(message);
	}

	
}
