package com.order.demo.infrastructure.persistence.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.order.demo.infrastructure.persistence.entities.OrderJpaEntity;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {
    Optional<OrderJpaEntity> findByExternalOrderId(String externalOrderId);
}