package com.companyz.accountmanagementsystem.exception;


//@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ApiRequestException extends  RuntimeException{
    public ApiRequestException(String msg) {
        super(msg);
    }
    public ApiRequestException(String msg,Throwable cause) {
        super(msg,cause);
    }
}
