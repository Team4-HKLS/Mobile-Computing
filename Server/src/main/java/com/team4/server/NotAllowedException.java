package com.team4.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class NotAllowedException extends RuntimeException  {
	
	/**
	 * 
	 */
	public NotAllowedException(String message) {
		super(message);
	}
}
