package com.dev.dscatalog2.controllers.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dev.dscatalog2.services.exception.ResourceNotFoundException;

@ControllerAdvice
public class ExceptionHandlerController {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException ex, HttpServletRequest request){
		
		StandardError error = new StandardError();
		error.setTimeStamp(Instant.now());
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setError("Resource not found");
		error.setMessage(ex.getMessage());
		error.setPath(request.getRequestURI());
		
		return ResponseEntity.status(error.getStatus()).body(error);
	}

}
