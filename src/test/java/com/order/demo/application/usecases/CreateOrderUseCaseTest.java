package com.order.demo.application.usecases;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.order.demo.application.dtos.CreateOrderCommand;
import com.order.demo.application.dtos.OrderItemCommand;
import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.exceptions.OrderAlreadyExistsException;
import com.order.demo.domain.repositories.OrderRepository;
import com.order.demo.domain.valueobjects.ExternalOrderId;
import com.order.demo.infrastructure.messaging.ProcessedOrderSender;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProcessedOrderSender processedOrderSender;

    private CreateOrderUseCase createOrderUseCase;

    @BeforeEach
    void setUp() {
        createOrderUseCase = new CreateOrderUseCase(orderRepository, processedOrderSender);
    }

    @Test
    void shouldSuccessfullyCreateAndProcessOrder() {
        // Arrange
        CreateOrderCommand command = createValidCommand();
        
        // Criar uma instância real de Order
        Order order = Order.create(command.externalOrderId());
        
        when(orderRepository.findByExternalOrderId(any())).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        // Act
        createOrderUseCase.execute(command);

        // Assert
        verify(orderRepository).findByExternalOrderId(command.externalOrderId());
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(processedOrderSender).sendProcessedOrder(any(Order.class));
        
        // Verificar o status final
        assertEquals(command.externalOrderId(), order.getExternalOrderId());
    }

    @Test
    void shouldThrowExceptionWhenOrderAlreadyExists() {
        // Arrange
        CreateOrderCommand command = createValidCommand();
        Order existingOrder = Order.create(command.externalOrderId());
        when(orderRepository.findByExternalOrderId(any())).thenReturn(Optional.of(existingOrder));

        // Act & Assert
        assertThatThrownBy(() -> createOrderUseCase.execute(command))
            .isInstanceOf(OrderAlreadyExistsException.class)
            .hasMessageContaining("Order already exists");

        verify(orderRepository).findByExternalOrderId(command.externalOrderId());
        verify(orderRepository, never()).save(any());
        verify(processedOrderSender, never()).sendProcessedOrder(any());
    }

    private CreateOrderCommand createValidCommand() {
        OrderItemCommand itemCommand = new OrderItemCommand(
            "PROD-001",
            "Test Product",
            2,
            BigDecimal.valueOf(100.00)
        );

        return new CreateOrderCommand(
            new ExternalOrderId("ORDER-001"),
            List.of(itemCommand)
        );
    }
}