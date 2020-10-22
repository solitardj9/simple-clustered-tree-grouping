package com.example.demo.application.systemInterface.imdgInterface.model.exception;

import org.springframework.http.HttpStatus;

public class ExceptionHazelcastServerConfigError extends Exception{
    //
	private static final long serialVersionUID = -8641117992385689736L;
	
	private final int errCode;
	
	private final HttpStatus httpStatus;
	
	public ExceptionHazelcastServerConfigError() {
		//
    	super(ExceptionInMemoryManagerCode.Config_Not_Found.getMessage());
    	errCode = ExceptionInMemoryManagerCode.Config_Not_Found.getCode();
    	httpStatus = ExceptionInMemoryManagerCode.Config_Not_Found.getHttpStatus();
    }
    
	public ExceptionHazelcastServerConfigError(Throwable cause) {
		//
		super(ExceptionInMemoryManagerCode.Config_Not_Found.getMessage(), cause);
		errCode = ExceptionInMemoryManagerCode.Config_Not_Found.getCode();
		httpStatus = ExceptionInMemoryManagerCode.Config_Not_Found.getHttpStatus();
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