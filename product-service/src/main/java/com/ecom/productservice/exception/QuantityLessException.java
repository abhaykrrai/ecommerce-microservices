package com.ecom.productservice.exception;

public class QuantityLessException extends RuntimeException{
    public QuantityLessException(String s){
        super(s);
    }
}
