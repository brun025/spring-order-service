package com.order.demo.infrastructure.rest.responses;

import java.util.List;

public record ErrorResponse(List<String> errors) {
    public static ErrorResponse of(String error) {
        return new ErrorResponse(List.of(error));
    }
}