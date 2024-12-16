package com.order.demo.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.demo.application.dtos.CreateOrderCommand;
import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.enums.OrderStatus;
import com.order.demo.domain.exceptions.OrderAlreadyExistsException;
import com.order.demo.domain.exceptions.OrderProcessingException;
import com.order.demo.domain.repositories.OrderRepository;
import com.order.demo.domain.valueobjects.Money;
import com.order.demo.domain.valueobjects.OrderId;
import com.order.demo.domain.valueobjects.ProductId;
import com.order.demo.infrastructure.messaging.ProcessedOrderSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateOrderUseCase {
    private final OrderRepository orderRepository;
    private final ProcessedOrderSender processedOrderSender;

    public OrderId execute(CreateOrderCommand command) {
        log.info("Processing order: {}", command.externalOrderId().getValue());

        // Verifica duplicidade
        orderRepository.findByExternalOrderId(command.externalOrderId())
            .ifPresent(existingOrder -> {
                throw new OrderAlreadyExistsException(command.externalOrderId());
            });

        try {
            // Cria ordem com status RECEIVED
            Order order = Order.create(command.externalOrderId());
            order = orderRepository.save(order);
            
            // Atualiza para PROCESSING
            orderRepository.updateStatus(order.getId(), OrderStatus.PROCESSING);

            // Adiciona itens e calcula
            final Order finalOrder = order;
            command.items().forEach(itemCommand -> {
                finalOrder.addItem(
                    new ProductId(itemCommand.productId()),
                    itemCommand.productName(),
                    itemCommand.quantity(),
                    new Money(itemCommand.unitPrice())
                );
            });

            // Atualiza para CALCULATED após processamento
            orderRepository.save(finalOrder);
            orderRepository.updateStatus(order.getId(), OrderStatus.CALCULATED);

            // Envia para Produto Externo B
            processedOrderSender.sendProcessedOrder(order);

            // Atualizar status final
            orderRepository.updateStatus(order.getId(), OrderStatus.COMPLETED);

            log.info("Order processed successfully: {}", order.getId().getValue());

            return order.getId();

        } catch (Exception e) {
            log.error("Error processing order", e);
            throw new OrderProcessingException(e);
        }
    }
}