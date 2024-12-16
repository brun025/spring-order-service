package com.order.demo.application.usecases;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.order.demo.application.dtos.OrderResponse;
import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.enums.OrderStatus;
import com.order.demo.domain.exceptions.OrderNotFoundException;
import com.order.demo.domain.repositories.OrderRepository;
import com.order.demo.domain.valueobjects.Money;
import com.order.demo.domain.valueobjects.OrderId;

@ExtendWith(MockitoExtension.class)
class GetOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    private GetOrderUseCase getOrderUseCase;

    @BeforeEach
    void setUp() {
        getOrderUseCase = new GetOrderUseCase(orderRepository);
    }

    @Test
    void execute_WhenOrderExists_ShouldReturnOrderResponse() {
        // Arrange
        UUID id = UUID.randomUUID();
        OrderId orderId = new OrderId(id);
        
        Order order = mock(Order.class);
        when(order.getId()).thenReturn(orderId);
        when(order.getExternalOrderId()).thenReturn(mock());
        when(order.getStatus()).thenReturn(OrderStatus.RECEIVED);
        when(order.getTotalAmount()).thenReturn(new Money(BigDecimal.valueOf(100.00)));
        when(order.getItems()).thenReturn(Collections.emptySet());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        OrderResponse response = getOrderUseCase.execute(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals(OrderStatus.RECEIVED, response.status());
        assertEquals(BigDecimal.valueOf(100.00), response.totalAmount());
        assertTrue(response.items().isEmpty());
        
        verify(orderRepository).findById(orderId);
    }

    @Test
    void execute_WhenOrderDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID());
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        OrderNotFoundException exception = assertThrows(
            OrderNotFoundException.class,
            () -> getOrderUseCase.execute(orderId)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(orderId.getValue().toString()));
        verify(orderRepository).findById(orderId);
    }

    @Test
    void execute_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID());
        when(orderRepository.findById(orderId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(
            RuntimeException.class,
            () -> getOrderUseCase.execute(orderId)
        );
        
        verify(orderRepository).findById(orderId);
    }
}