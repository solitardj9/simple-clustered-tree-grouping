package com.example.demo.application.systemInterface.imdgInterface.model.exception;

import org.springframework.http.HttpStatus;

public class ExceptionHazelcastIMapBadRequest extends Exception{
	//
	private static final long serialVersionUID = -3079799474969688504L;

	private final int errCode;
	
	private final HttpStatus httpStatus;
	
	public ExceptionHazelcastIMapBadRequest() {
		//
    	super(ExceptionInMemoryManagerCode.Bad_Request.getMessage());
    	errCode = ExceptionInMemoryManagerCode.Bad_Request.getCode();
    	httpStatus = ExceptionInMemoryManagerCode.Bad_Request.getHttpStatus();
    }
    
	public ExceptionHazelcastIMapBadRequest(Throwable cause) {
		//
		super(ExceptionInMemoryManagerCode.Bad_Request.getMessage(), cause);
		errCode = ExceptionInMemoryManagerCode.Bad_Request.getCode();
		httpStatus = ExceptionInMemoryManagerCode.Bad_Request.getHttpStatus();
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