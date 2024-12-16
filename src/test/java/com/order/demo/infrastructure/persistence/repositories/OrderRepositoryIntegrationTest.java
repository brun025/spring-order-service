package com.order.demo.infrastructure.persistence.repositories;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.order.demo.domain.enums.OrderStatus;
import com.order.demo.infrastructure.persistence.AbstractIntegrationTest;
import com.order.demo.infrastructure.persistence.entities.OrderItemJpaEntity;
import com.order.demo.infrastructure.persistence.entities.OrderJpaEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @Import(PersistenceOrderMapper.class)
class OrderRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderJpaRepository orderRepository;

    // @Autowired
    // private PersistenceOrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveOrder() {
        // Arrange
        OrderJpaEntity order = createOrderEntity();
        
        // Act
        OrderJpaEntity savedOrder = orderRepository.save(order);
        Optional<OrderJpaEntity> foundOrder = orderRepository.findById(savedOrder.getId());
        
        // Assert
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getExternalOrderId()).isEqualTo(order.getExternalOrderId());
        assertThat(foundOrder.get().getItems()).hasSize(order.getItems().size());
    }

    @Test
    void shouldFindByExternalOrderId() {
        // Arrange
        OrderJpaEntity order = createOrderEntity();
        orderRepository.save(order);
        
        // Act
        Optional<OrderJpaEntity> foundOrder = orderRepository.findByExternalOrderId(order.getExternalOrderId());
        
        // Assert
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getExternalOrderId()).isEqualTo(order.getExternalOrderId());
    }

    @Test
    void shouldNotFindByNonExistentExternalOrderId() {
        // Act
        Optional<OrderJpaEntity> foundOrder = orderRepository.findByExternalOrderId("NON-EXISTENT");
        
        // Assert
        assertThat(foundOrder).isEmpty();
    }

    @Test
    void shouldSaveOrderWithItems() {
        // Arrange
        OrderJpaEntity order = createOrderEntity();
        order.setItems(createOrderItems(order));
        
        // Act
        OrderJpaEntity savedOrder = orderRepository.save(order);
        Optional<OrderJpaEntity> foundOrder = orderRepository.findById(savedOrder.getId());
        
        // Assert
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getItems()).hasSize(2);
        assertThat(foundOrder.get().getItems())
            .extracting(OrderItemJpaEntity::getProductId)
            .containsExactlyInAnyOrder("PROD-001", "PROD-002");
    }

    @Test
    void shouldUpdateExistingOrder() {
        // Arrange
        OrderJpaEntity order = createOrderEntity();
        order.setStatus(OrderStatus.RECEIVED);
        OrderJpaEntity savedOrder = orderRepository.save(order);
        
        // Act
        savedOrder.setStatus(OrderStatus.COMPLETED);
        OrderJpaEntity updatedOrder = orderRepository.save(savedOrder);
        
        // Assert
        Optional<OrderJpaEntity> foundOrder = orderRepository.findById(updatedOrder.getId());
        assertThat(foundOrder)
            .isPresent()
            .get()
            .extracting(OrderJpaEntity::getStatus)
            .isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void shouldReturnEmptyWhenFindingNonExistentOrder() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        
        // Act
        Optional<OrderJpaEntity> result = orderRepository.findById(nonExistentId);
        
        // Assert
        assertThat(result).isEmpty();
    }

    private OrderJpaEntity createOrderEntity() {
        OrderJpaEntity order = new OrderJpaEntity();
        order.setId(UUID.randomUUID());
        order.setExternalOrderId("ORDER-001");
        order.setStatus(OrderStatus.RECEIVED);
        order.setTotalAmount(BigDecimal.valueOf(100.00));
        return order;
    }

    private Set<OrderItemJpaEntity> createOrderItems(OrderJpaEntity order) {
        Set<OrderItemJpaEntity> items = new HashSet<>();
        
        OrderItemJpaEntity item1 = new OrderItemJpaEntity();
        item1.setProductId("PROD-001");
        item1.setProductName("Product 1");
        item1.setQuantity(2);
        item1.setUnitPrice(BigDecimal.valueOf(30.00));
        item1.setTotalPrice(BigDecimal.valueOf(60.00));
        item1.setOrder(order);
        items.add(item1);
        
        OrderItemJpaEntity item2 = new OrderItemJpaEntity();
        item2.setProductId("PROD-002");
        item2.setProductName("Product 2");
        item2.setQuantity(1);
        item2.setUnitPrice(BigDecimal.valueOf(40.00));
        item2.setTotalPrice(BigDecimal.valueOf(40.00));
        item2.setOrder(order);
        items.add(item2);
        
        return items;
    }
}
