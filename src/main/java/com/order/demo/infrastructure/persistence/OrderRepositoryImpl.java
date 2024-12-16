package com.order.demo.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.enums.OrderStatus;
import com.order.demo.domain.exceptions.OrderNotFoundException;
import com.order.demo.domain.repositories.OrderRepository;
import com.order.demo.domain.valueobjects.ExternalOrderId;
import com.order.demo.domain.valueobjects.OrderId;
import com.order.demo.infrastructure.persistence.entities.OrderJpaEntity;
import com.order.demo.infrastructure.persistence.repositories.OrderJpaRepository;
import com.order.demo.infrastructure.persistence.repositories.PersistenceOrderMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpaRepository;
    private final PersistenceOrderMapper mapper;

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return jpaRepository.findById(orderId.getValue())
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByExternalOrderId(ExternalOrderId externalOrderId) {
        return jpaRepository.findByExternalOrderId(externalOrderId.getValue())
            .map(mapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = mapper.toEntity(order);
        OrderJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void updateStatus(OrderId orderId, OrderStatus status) {
        OrderJpaEntity order = jpaRepository.findById(orderId.getValue())
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(status);
        jpaRepository.save(order);
    }
    
}