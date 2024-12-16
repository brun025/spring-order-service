package com.order.demo.domain.exceptions;

public class OrderProcessingException extends DomainException {
    public OrderProcessingException(Exception e) {
        super("Error processing order " + e.getMessage());
    }
}
