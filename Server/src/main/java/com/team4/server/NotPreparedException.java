package com.team4.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.ACCEPTED)
public class NotPreparedException extends RuntimeException {

    public NotPreparedException(String message) {
		super(message);
	}
}
