package com.order.demo.domain.aggregates;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.order.demo.domain.entities.OrderItem;
import com.order.demo.domain.enums.OrderStatus;
import com.order.demo.domain.events.DomainEvent;
import com.order.demo.domain.valueobjects.ExternalOrderId;
import com.order.demo.domain.valueobjects.Money;
import com.order.demo.domain.valueobjects.OrderId;
import com.order.demo.domain.valueobjects.ProductId;

import lombok.Getter;


@Getter
public class Order {
    private OrderId id;
    private ExternalOrderId externalOrderId;
    private OrderStatus status;
    private Set<OrderItem> items;
    private Money totalAmount;
    private final List<DomainEvent> domainEvents;

    private Order() {
        this.items = new HashSet<>();
        this.domainEvents = new ArrayList<>();
        this.totalAmount = new Money(BigDecimal.ZERO);
    }

    public static Order create(ExternalOrderId externalOrderId) {
        Order order = new Order();
        order.id = OrderId.generate();
        order.externalOrderId = externalOrderId;
        order.status = OrderStatus.RECEIVED;
        return order;
    }

    public void addItem(ProductId productId, String productName, int quantity, Money unitPrice) {
        
        OrderItem item = new OrderItem(productId, productName, quantity, unitPrice);
        items.add(item);
        recalculateTotal();
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
            .map(OrderItem::getTotalPrice)
            .reduce(new Money(BigDecimal.ZERO), Money::add);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    public static Order reconstitute(
            OrderId id,
            ExternalOrderId externalOrderId,
            OrderStatus status,
            Set<OrderItem> items,
            Money totalAmount) {
        
        Order order = new Order();
        order.id = id;
        order.externalOrderId = externalOrderId;
        order.status = status;
        order.items = new HashSet<>(items);
        order.totalAmount = totalAmount;
        
        return order;
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

}