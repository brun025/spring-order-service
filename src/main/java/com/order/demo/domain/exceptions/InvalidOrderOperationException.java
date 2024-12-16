package com.order.demo.domain.exceptions;

public class InvalidOrderOperationException extends DomainException {
    public InvalidOrderOperationException(String message) {
        super(message);
    }
}
