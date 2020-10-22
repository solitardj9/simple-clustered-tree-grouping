package com.example.demo.application.systemInterface.imdgInterface.model.exception;

import org.springframework.http.HttpStatus;

public class ExceptionHazelcastServerAlreadyClosed extends Exception{
    //
	private static final long serialVersionUID = 788175780668151154L;

	private final int errCode;
	
	private final HttpStatus httpStatus;
	
	public ExceptionHazelcastServerAlreadyClosed() {
		//
    	super(ExceptionInMemoryManagerCode.Already_Closed.getMessage());
    	errCode = ExceptionInMemoryManagerCode.Already_Closed.getCode();
    	httpStatus = ExceptionInMemoryManagerCode.Already_Closed.getHttpStatus();
    }
    
	public ExceptionHazelcastServerAlreadyClosed(Throwable cause) {
		//
		super(ExceptionInMemoryManagerCode.Already_Closed.getMessage(), cause);
		errCode = ExceptionInMemoryManagerCode.Already_Closed.getCode();
		httpStatus = ExceptionInMemoryManagerCode.Already_Closed.getHttpStatus();
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