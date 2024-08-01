package com.apboutos.spendy.spendyapi.exception;

import lombok.Getter;

@Getter
public class TokenExpiredException extends Exception{
    private final String message;

    public TokenExpiredException(String message){
        super(message);
        this.message = message;
    }
}
