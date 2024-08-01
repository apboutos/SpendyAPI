package com.apboutos.spendy.spendyapi.response;

import lombok.Getter;

@Getter
public class UserNotSavedException extends Exception{

    private final String message;

    public UserNotSavedException(String message){
        super(message);
        this.message = message;
    }
}
