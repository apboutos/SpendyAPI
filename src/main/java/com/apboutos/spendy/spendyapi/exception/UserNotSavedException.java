package com.apboutos.spendy.spendyapi.exception;

import lombok.Getter;

@Getter
public class UserNotSavedException extends Exception{

    private final String message;

    public UserNotSavedException(String message){
        super(message);
        this.message = message;
    }
}
