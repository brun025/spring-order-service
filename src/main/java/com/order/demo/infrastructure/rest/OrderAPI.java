package com.order.demo.infrastructure.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.order.demo.application.dtos.OrderResponse;
import com.order.demo.infrastructure.rest.validation.ValidUUID;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Orders") 
@RequestMapping("/api/orders")
@Validated
public interface OrderAPI {

        @GetMapping("/test")
        public ResponseEntity<String> test();

        @GetMapping(
                value = "{orderId}",
                produces = MediaType.APPLICATION_JSON_VALUE
        )
        @Parameter(
                name = "orderId",
                description = "Order UUID",
                example = "123e4567-e89b-12d3-a456-426614174000",
                in = ParameterIn.PATH,
                required = true
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
                @ApiResponse(responseCode = "404", description = "Order was not found"),
                @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
        })
        ResponseEntity<OrderResponse> getOrder(@ValidUUID @PathVariable String orderId);
}