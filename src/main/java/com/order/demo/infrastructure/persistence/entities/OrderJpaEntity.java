package com.order.demo.infrastructure.persistence.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.order.demo.domain.enums.OrderStatus;
import com.order.demo.infrastructure.persistence.types.PostgreSQLEnumType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderJpaEntity {
    @Id
    @Column(name = "id")
    private UUID id;  // UUID direto

    @Column(name = "external_order_id")
    private String externalOrderId; 

    @Column(name = "status", columnDefinition = "order_status")
    @Type(PostgreSQLEnumType.class)
    private OrderStatus status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItemJpaEntity> items = new HashSet<>();

    public void addItem(OrderItemJpaEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItemJpaEntity item) {
        items.remove(item);
        item.setOrder(null);
    }
}