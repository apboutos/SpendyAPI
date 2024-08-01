package com.apboutos.spendy.spendyapi.exception;

import lombok.Getter;

@Getter
public class UsernameTakenException extends Exception{

    private final String message;

    public UsernameTakenException(String message){
        super(message);
        this.message = message;
    }
}
