package com.order.demo.infrastructure.persistence.repositories;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.entities.OrderItem;
import com.order.demo.domain.valueobjects.ExternalOrderId;
import com.order.demo.domain.valueobjects.Money;
import com.order.demo.domain.valueobjects.OrderId;
import com.order.demo.domain.valueobjects.ProductId;
import com.order.demo.infrastructure.persistence.entities.OrderItemJpaEntity;
import com.order.demo.infrastructure.persistence.entities.OrderJpaEntity;

@Component
public class PersistenceOrderMapper {
    public OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(order.getId().getValue());
        entity.setExternalOrderId(order.getExternalOrderId().getValue());
        entity.setStatus(order.getStatus());
        entity.setTotalAmount(order.getTotalAmount().getAmount());
        
        Set<OrderItemJpaEntity> itemEntities = order.getItems().stream()
            .map(item -> toOrderItemEntity(item, entity))
            .collect(Collectors.toSet());
        
        entity.setItems(itemEntities);
        return entity;
    }

    public Order toDomain(OrderJpaEntity entity) {
        Set<OrderItem> items = entity.getItems().stream()
            .map(this::toOrderItem)
            .collect(Collectors.toSet());

        return Order.reconstitute(
            OrderId.of(entity.getId()),  // Criando OrderId do UUID
            new ExternalOrderId(entity.getExternalOrderId()),
            entity.getStatus(),
            items,
            new Money(entity.getTotalAmount())
        );
    }

    private OrderItemJpaEntity toOrderItemEntity(OrderItem item, OrderJpaEntity order) {
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        entity.setProductId(item.getProductId().getValue());
        entity.setProductName(item.getProductName());
        entity.setQuantity(item.getQuantity());
        entity.setUnitPrice(item.getUnitPrice().getAmount());
        entity.setTotalPrice(item.getTotalPrice().getAmount());
        entity.setOrder(order);
        return entity;
    }

    private OrderItem toOrderItem(OrderItemJpaEntity entity) {
        return new OrderItem(
            new ProductId(entity.getProductId()),
            entity.getProductName(),
            entity.getQuantity(),
            new Money(entity.getUnitPrice())
        );
    }
}