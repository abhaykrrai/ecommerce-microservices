package com.com.cartservice.exception;

public class ProductIsLessOrderException extends RuntimeException{
    public ProductIsLessOrderException(String msg){
        super(msg);
    }
}
