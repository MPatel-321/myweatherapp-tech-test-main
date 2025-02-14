package com.weatherapp.myweatherapp.exception;

import org.springframework.http.HttpStatus;

public class ExceptionResponse {
	private String message;
	private String description;
	private HttpStatus httpStatus;
	private String timestamp;
	
	public ExceptionResponse(String message, String description, HttpStatus httpStatus, String timestamp) {
		this.message = message;
		this.description = description;
		this.httpStatus = httpStatus;
		this.timestamp = timestamp;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
