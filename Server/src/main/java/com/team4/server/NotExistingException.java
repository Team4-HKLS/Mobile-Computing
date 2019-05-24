package com.team4.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

<<<<<<< HEAD
@ResponseStatus(value = HttpStatus.NOT_FOUND)
=======
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
>>>>>>> 8b035a0514dc41a721f7e548fbed8df8c1b001c0
public class NotExistingException extends RuntimeException  {
	
	/**
	 * 
	 */
	public NotExistingException(String message) {
		super(message);
	}
}
