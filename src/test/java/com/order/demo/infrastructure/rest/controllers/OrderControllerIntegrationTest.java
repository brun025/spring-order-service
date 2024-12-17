package com.order.demo.infrastructure.rest.controllers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.order.demo.application.dtos.OrderResponse;
import com.order.demo.config.TestConfig;
import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.valueobjects.ExternalOrderId;
import com.order.demo.domain.valueobjects.Money;
import com.order.demo.domain.valueobjects.ProductId;
import com.order.demo.infrastructure.persistence.AbstractIntegrationTest;
import com.order.demo.infrastructure.persistence.entities.OrderJpaEntity;
import com.order.demo.infrastructure.persistence.repositories.OrderJpaRepository;
import com.order.demo.infrastructure.persistence.repositories.PersistenceOrderMapper;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.kafka.enabled=false",
        "spring.kafka.bootstrap-servers=",
        "spring.main.allow-bean-definition-overriding=true"
    }
)
@Import(TestConfig.class)
class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderJpaRepository orderRepository;
    
    @Autowired
    private PersistenceOrderMapper orderMapper;

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldReturnBadRequestWhenInvalidUUID() throws Exception {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/orders/invalid-uuid", 
            String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnOrderWhenExists() {
        // Arrange
        Order order = createAndSaveOrder();

        // Act
        ResponseEntity<OrderResponse> response = restTemplate.getForEntity(
            "/api/orders/{orderId}", 
            OrderResponse.class, 
            order.getId().getValue()
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().externalOrderId()).isEqualTo("ORDER-E2E-001");
        assertThat(response.getBody().items().get(0).productId()).isEqualTo("PROD-001");
        assertThat(response.getBody().items().get(0).quantity()).isEqualTo(1);
        assertThat(response.getBody().items().get(0).unitPrice().compareTo(BigDecimal.TEN)).isEqualTo(0);
    }

    @Test
    void shouldReturnNotFoundForNonExistentOrder() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/orders/{orderId}", 
            String.class,
            UUID.randomUUID()
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Order createAndSaveOrder() {
        Order order = Order.create(new ExternalOrderId("ORDER-E2E-001"));
        
        order.addItem(
            new ProductId("PROD-001"),
            "Test Product",
            1,
            new Money(BigDecimal.TEN)
        );

        OrderJpaEntity entity = orderMapper.toEntity(order);
        OrderJpaEntity savedEntity = orderRepository.save(entity);
        return orderMapper.toDomain(savedEntity);
    }
}