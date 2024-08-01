package com.apboutos.spendy.spendyapi.exception;

import lombok.Getter;

@Getter
public class UsernameNotFoundException extends Exception {

    private final String message;

    public UsernameNotFoundException(String message){
        super(message);
        this.message = message;
    }
}
