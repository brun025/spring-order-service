package com.order.demo.application.usecases;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.order.demo.application.dtos.OrderResponse;
import com.order.demo.domain.exceptions.OrderNotFoundException;
import com.order.demo.domain.repositories.OrderRepository;
import com.order.demo.domain.valueobjects.OrderId;

import lombok.RequiredArgsConstructor;

@Service
@Cacheable(value = "orders", key = "#orderId.value")
@RequiredArgsConstructor

public class GetOrderUseCase {
    private final OrderRepository orderRepository;

    public OrderResponse execute(OrderId orderId) {
        return orderRepository.findById(orderId)
            .map(OrderResponse::from)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
