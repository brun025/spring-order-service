package com.order.demo.infrastructure.config;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.order.demo.domain.valueobjects.OrderId;

public class OrderIdDeserializer extends JsonDeserializer<OrderId> {
    @Override
    public OrderId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String value = node.get("value").asText();
        return OrderId.of(UUID.fromString(value));
    }
}