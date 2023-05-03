package com.companyz.accountmanagementsystem.exception;

public class NotFoundException extends ApiRequestException{
    public NotFoundException(String subject) {
        super("Sorry "+subject+" is not found");
    }
}
