package com.order.demo.infrastructure.messaging;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.events.ProcessedOrderEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessedOrderSender {
    
    private final KafkaTemplate<String, ProcessedOrderEvent> kafkaTemplate;
    
    @Value("${spring.kafka.producer.processed-orders-topic}")
    private String processedOrdersTopic;

    public void sendProcessedOrder(Order order) {
        try {
            ProcessedOrderEvent event = createEventFromOrder(order);
            kafkaTemplate.send(processedOrdersTopic, event.getExternalOrderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully sent processed order: {}", event.getExternalOrderId());
                    } else {
                        log.error("Error sending processed order: {}", event.getExternalOrderId(), ex);
                    }
                });
        } catch (Exception e) {
            log.error("Error creating processed order event: {}", order.getExternalOrderId(), e);
        }
    }

    private ProcessedOrderEvent createEventFromOrder(Order order) {
        List<ProcessedOrderEvent.ProcessedOrderItemEvent> items = order.getItems().stream()
            .map(item -> new ProcessedOrderEvent.ProcessedOrderItemEvent(
                item.getProductId().getValue(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice().getAmount(),
                item.getTotalPrice().getAmount()
            ))
            .collect(Collectors.toList());

        return new ProcessedOrderEvent(
            order.getExternalOrderId().getValue(),
            order.getStatus().name(),
            order.getTotalAmount().getAmount(),
            items
        );
    }
}