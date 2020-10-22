package com.example.demo.application.systemInterface.imdgInterface.model.exception;

import org.springframework.http.HttpStatus;


public enum ExceptionInMemoryManagerCode {
    //
	Already_Opened(409, "AlreadyOpenedException.", HttpStatus.CONFLICT),
	Already_Closed(409, "AlreadyClosedException.", HttpStatus.CONFLICT),
	Same_Name_Distrbuted_Object_Alreay_Exist(409, "SameNameDistrbutedObjectIsAlreayExist.", HttpStatus.CONFLICT),
	Bad_Request(400, "BadRequest.", HttpStatus.BAD_REQUEST),
	Config_Not_Found(404, "ConfigFileNotFound.", HttpStatus.NOT_FOUND),
	IMap_Not_Found(404, "IMapNotFound.", HttpStatus.NOT_FOUND),
	Internal_Failure(500, "InternalFailureException.", HttpStatus.INTERNAL_SERVER_ERROR)
    ;
 
    private Integer code;
    private String message;
    private HttpStatus httpStatus;
 
    ExceptionInMemoryManagerCode(Integer code, String msg, HttpStatus httpStatus) {
        this.code = code;
        this.message = msg;
        this.httpStatus = httpStatus;
    }
    
    public Integer getCode() {
        return this.code;
    }
    
    public String getMessage() {
        return this.message;
    }

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}