package com.example.demo.application.systemInterface.imdgInterface.model.exception;

import org.springframework.http.HttpStatus;

public class ExceptionHazelcastDistributedObjectNameConflict extends Exception{
    //
	private static final long serialVersionUID = 4129725094191320283L;

	private final int errCode;
	
	private final HttpStatus httpStatus;
	
	public ExceptionHazelcastDistributedObjectNameConflict() {
		//
    	super(ExceptionInMemoryManagerCode.Same_Name_Distrbuted_Object_Alreay_Exist.getMessage());
    	errCode = ExceptionInMemoryManagerCode.Same_Name_Distrbuted_Object_Alreay_Exist.getCode();
    	httpStatus = ExceptionInMemoryManagerCode.Same_Name_Distrbuted_Object_Alreay_Exist.getHttpStatus();
    }
    
	public ExceptionHazelcastDistributedObjectNameConflict(Throwable cause) {
		//
		super(ExceptionInMemoryManagerCode.Same_Name_Distrbuted_Object_Alreay_Exist.getMessage(), cause);
		errCode = ExceptionInMemoryManagerCode.Same_Name_Distrbuted_Object_Alreay_Exist.getCode();
		httpStatus = ExceptionInMemoryManagerCode.Same_Name_Distrbuted_Object_Alreay_Exist.getHttpStatus();
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