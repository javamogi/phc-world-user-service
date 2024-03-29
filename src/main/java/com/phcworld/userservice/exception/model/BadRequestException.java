package com.phcworld.userservice.exception.model;

public class BadRequestException extends CustomBaseException{
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public BadRequestException(){
        super(ErrorCode.BAD_REQUEST);
    }
}
