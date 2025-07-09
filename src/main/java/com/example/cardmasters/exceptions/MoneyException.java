package com.example.cardmasters.exceptions;

public class MoneyException extends RuntimeException {

    public MoneyException(String message) {
        super(message);
    }

    public MoneyException(String message, Throwable cause) {
        super(message, cause);
    }
}
