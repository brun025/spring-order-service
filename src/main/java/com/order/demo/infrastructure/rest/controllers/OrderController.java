package com.order.demo.infrastructure.rest.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.order.demo.application.dtos.OrderResponse;
import com.order.demo.application.usecases.GetOrderUseCase;
import com.order.demo.domain.valueobjects.OrderId;
import com.order.demo.infrastructure.rest.OrderAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController implements OrderAPI{
    private final GetOrderUseCase getOrderUseCase;

    @Override
    public ResponseEntity<String> test() {
        log.info("Test endpoint called");
        return ResponseEntity.ok("Test endpoint working!");
    }

    @Override
    public ResponseEntity<OrderResponse> getOrder(final String orderId) {
        OrderResponse response = getOrderUseCase.execute(OrderId.of(UUID.fromString(orderId)));
        return ResponseEntity.ok(response);
    }
}