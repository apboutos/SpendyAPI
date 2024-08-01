package com.apboutos.spendy.spendyapi.exception;

import lombok.Getter;

@Getter
public class CategoryHasEntriesException extends Exception{

    private final String message;

    public CategoryHasEntriesException(String message){
        super(message);
        this.message = message;
    }

}