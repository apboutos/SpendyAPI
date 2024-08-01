package com.apboutos.spendy.spendyapi.exception;

import lombok.Getter;

@Getter
public class CategoryExistsException extends Exception{

    private final String message;

    public CategoryExistsException(String message){
        super(message);
        this.message = message;
    }

}
