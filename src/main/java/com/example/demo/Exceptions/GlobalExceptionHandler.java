package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler handles application-wide exceptions
 * and returns appropriate HTTP responses with meaningful messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	//Handles custom exception ResourceNotFoundException and returns 404 status code
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> GlobalException(ResourceNotFoundException ex)
	{
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	//Handles custom exception ProductStockException and returns 400 status code
	@ExceptionHandler(ProductStockException.class) 
	public ResponseEntity<String> handleProductStockException(ProductStockException ex) 
	{ return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); }
}
