package com.example.demo.application.systemInterface.imdgInterface.model.exception;

import org.springframework.http.HttpStatus;

public class ExceptionHazelcastIMapNotFound extends Exception{
	//
	private static final long serialVersionUID = 8460612198133957148L;
	
	private final int errCode;
	
	private final HttpStatus httpStatus;
	
	public ExceptionHazelcastIMapNotFound() {
		//
    	super(ExceptionInMemoryManagerCode.IMap_Not_Found.getMessage());
    	errCode = ExceptionInMemoryManagerCode.IMap_Not_Found.getCode();
    	httpStatus = ExceptionInMemoryManagerCode.IMap_Not_Found.getHttpStatus();
    }
    
	public ExceptionHazelcastIMapNotFound(Throwable cause) {
		//
		super(ExceptionInMemoryManagerCode.IMap_Not_Found.getMessage(), cause);
		errCode = ExceptionInMemoryManagerCode.IMap_Not_Found.getCode();
		httpStatus = ExceptionInMemoryManagerCode.IMap_Not_Found.getHttpStatus();
	}
	
	public int getErrCode() {
		//
		return errCode;
    }
	
	public HttpStatus getHttpStatus() {
		//
		return httpStatus;
    }
}