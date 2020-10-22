package com.example.demo.application.systemInterface.imdgInterface.model.exception;

import org.springframework.http.HttpStatus;

public class ExceptionHazelcastServerAlreadyOpened extends Exception{
    //
	private static final long serialVersionUID = 7594371506466712673L;
	
	private final int errCode;
	
	private final HttpStatus httpStatus;
	
	public ExceptionHazelcastServerAlreadyOpened() {
		//
    	super(ExceptionInMemoryManagerCode.Already_Opened.getMessage());
    	errCode = ExceptionInMemoryManagerCode.Already_Opened.getCode();
    	httpStatus = ExceptionInMemoryManagerCode.Already_Opened.getHttpStatus();
    }
    
	public ExceptionHazelcastServerAlreadyOpened(Throwable cause) {
		//
		super(ExceptionInMemoryManagerCode.Already_Opened.getMessage(), cause);
		errCode = ExceptionInMemoryManagerCode.Already_Opened.getCode();
		httpStatus = ExceptionInMemoryManagerCode.Already_Opened.getHttpStatus();
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