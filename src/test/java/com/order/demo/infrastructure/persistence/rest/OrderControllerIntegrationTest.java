package com.order.demo.infrastructure.persistence.rest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.order.demo.domain.aggregates.Order;
import com.order.demo.domain.valueobjects.ExternalOrderId;
import com.order.demo.domain.valueobjects.Money;
import com.order.demo.domain.valueobjects.ProductId;
import com.order.demo.infrastructure.persistence.AbstractIntegrationTest;
import com.order.demo.infrastructure.persistence.entities.OrderJpaEntity;
import com.order.demo.infrastructure.persistence.repositories.OrderJpaRepository;
import com.order.demo.infrastructure.persistence.repositories.PersistenceOrderMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderJpaRepository orderRepository;

    @Autowired
    private PersistenceOrderMapper mapper;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldReturnOrderWhenExists() throws Exception {
        // Arrange
        Order order = createAndSaveOrder();

        // Act & Assert
        mockMvc.perform(get("/api/orders/" + order.getId().getValue())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().getValue().toString()))
                .andExpect(jsonPath("$.externalOrderId").value(order.getExternalOrderId().getValue()))
                .andExpect(jsonPath("$.status").value(order.getStatus().name()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/orders/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").exists())
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders/invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andDo(print());
    }

    private Order createAndSaveOrder() {
        Order order = Order.create(new ExternalOrderId("ORDER-001"));
        
        order.addItem(new ProductId("PROD-001"),
        "Test Product",
        1,
        new Money(BigDecimal.TEN));

        OrderJpaEntity entity = mapper.toEntity(order);
        OrderJpaEntity savedEntity = orderRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}