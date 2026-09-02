package com.com.cartservice.exception;

public class CartAccessDeniedException extends RuntimeException {

    public CartAccessDeniedException(String msg) {
        super(msg);
    }
}