package com.sergiu.authenticationdemo.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

	@ExceptionHandler(value = { NoUserFoundException.class })
	public ResponseEntity<Object> handleInvalidInputException(NoUserFoundException e) {
		logger.error("User not found exception!: ", e.getMessage());
		return new ResponseEntity<Object>("User not found exception!: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { LoginException.class })
	public ResponseEntity<Object> handleInvalidInputException(LoginException e) {
		logger.error("Login exception!: ", e.getMessage());
		return new ResponseEntity<Object>("Login exception!: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { InternalServerError.class })
	public ResponseEntity<Object> handleInvalidInputException(InternalServerError e) {
		logger.error("Something went wrong with our app!: ", e.getMessage());
		return new ResponseEntity<Object>("Something went wrong with our app!: " + e.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> handleInvalidInputException(Exception e) {
		logger.error("Unexpected error!: ", e.getMessage());
		return new ResponseEntity<Object>("Unexpected error!", HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
