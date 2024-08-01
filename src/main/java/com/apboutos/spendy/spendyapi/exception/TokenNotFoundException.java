package com.apboutos.spendy.spendyapi.exception;

import lombok.Getter;

@Getter
public class TokenNotFoundException extends Exception{

    private final String message;

    public TokenNotFoundException(String message){
        super(message);
        this.message = message;
    }
}
