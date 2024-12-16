package com.order.demo.domain.exceptions;

import com.order.demo.domain.valueobjects.ExternalOrderId;

public class OrderAlreadyExistsException extends DomainException {
    public OrderAlreadyExistsException(ExternalOrderId externalOrderId) {
        super(String.format("Order already exists with external id: %s", externalOrderId.getValue()));
    }
}
