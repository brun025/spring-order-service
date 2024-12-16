package com.order.demo.infrastructure.persistence.repositories;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.entities.OrderItem;
import com.order.demo.domain.enums.OrderStatus;
import com.order.demo.domain.valueobjects.ExternalOrderId;
import com.order.demo.domain.valueobjects.Money;
import com.order.demo.domain.valueobjects.OrderId;
import com.order.demo.domain.valueobjects.ProductId;
import com.order.demo.infrastructure.persistence.entities.OrderItemJpaEntity;
import com.order.demo.infrastructure.persistence.entities.OrderJpaEntity;

@ExtendWith(MockitoExtension.class)
class PersistenceOrderMapperTest {

    private PersistenceOrderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PersistenceOrderMapper();
    }

    @Test
    void toEntity_ShouldMapAllFieldsCorrectly() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Set<OrderItem> items = new HashSet<>();
        items.add(new OrderItem(
            new ProductId("PROD-1"),
            "Product 1",
            2,
            new Money(BigDecimal.valueOf(100.00))
        ));

        Order order = Order.reconstitute(
            new OrderId(orderId),
            new ExternalOrderId("EXT-001"),
            OrderStatus.RECEIVED,
            items,
            new Money(BigDecimal.valueOf(200.00))
        );

        // Act
        OrderJpaEntity result = mapper.toEntity(order);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals("EXT-001", result.getExternalOrderId());
        assertEquals(OrderStatus.RECEIVED, result.getStatus());
        assertEquals(BigDecimal.valueOf(200.00), result.getTotalAmount());
        
        assertEquals(1, result.getItems().size());
        OrderItemJpaEntity itemEntity = result.getItems().iterator().next();
        assertEquals("PROD-1", itemEntity.getProductId());
        assertEquals("Product 1", itemEntity.getProductName());
        assertEquals(2, itemEntity.getQuantity());
        assertEquals(BigDecimal.valueOf(100.00), itemEntity.getUnitPrice());
        assertEquals(BigDecimal.valueOf(200.00), itemEntity.getTotalPrice());
        assertEquals(result, itemEntity.getOrder());
    }

    @Test
    void toDomain_ShouldMapAllFieldsCorrectly() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(orderId);
        entity.setExternalOrderId("EXT-001");
        entity.setStatus(OrderStatus.RECEIVED);
        entity.setTotalAmount(BigDecimal.valueOf(200.00));

        OrderItemJpaEntity itemEntity = new OrderItemJpaEntity();
        itemEntity.setProductId("PROD-1");
        itemEntity.setProductName("Product 1");
        itemEntity.setQuantity(2);
        itemEntity.setUnitPrice(BigDecimal.valueOf(100.00));
        itemEntity.setTotalPrice(BigDecimal.valueOf(200.00));
        itemEntity.setOrder(entity);

        Set<OrderItemJpaEntity> items = new HashSet<>();
        items.add(itemEntity);
        entity.setItems(items);

        // Act
        Order result = mapper.toDomain(entity);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId().getValue());
        assertEquals("EXT-001", result.getExternalOrderId().getValue());
        assertEquals(OrderStatus.RECEIVED, result.getStatus());
        assertEquals(BigDecimal.valueOf(200.00), result.getTotalAmount().getAmount());

        assertEquals(1, result.getItems().size());
        OrderItem item = result.getItems().iterator().next();
        assertEquals("PROD-1", item.getProductId().getValue());
        assertEquals("Product 1", item.getProductName());
        assertEquals(2, item.getQuantity());
        assertEquals(BigDecimal.valueOf(100.00), item.getUnitPrice().getAmount());
        assertEquals(BigDecimal.valueOf(200.00), item.getTotalPrice().getAmount());
    }

    @Test
    void toEntity_WithEmptyItems_ShouldMapCorrectly() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = Order.reconstitute(
            new OrderId(orderId),
            new ExternalOrderId("EXT-001"),
            OrderStatus.RECEIVED,
            new HashSet<>(),
            new Money(BigDecimal.valueOf(0.00))
        );

        // Act
        OrderJpaEntity result = mapper.toEntity(order);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void toDomain_WithEmptyItems_ShouldMapCorrectly() {
        // Arrange
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setExternalOrderId("EXT-001");
        entity.setStatus(OrderStatus.RECEIVED);
        entity.setTotalAmount(BigDecimal.valueOf(0.00));
        entity.setItems(new HashSet<>());

        // Act
        Order result = mapper.toDomain(entity);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }
}