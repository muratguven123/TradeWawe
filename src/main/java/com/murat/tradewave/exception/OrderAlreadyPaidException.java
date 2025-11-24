package com.murat.tradewave.exception;

public class OrderAlreadyPaidException extends RuntimeException {
    public OrderAlreadyPaidException(String message) {
        super(message);
    }
}

