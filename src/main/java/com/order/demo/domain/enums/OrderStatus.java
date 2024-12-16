package com.order.demo.domain.enums;

public enum OrderStatus {
    RECEIVED("Pedido recebido"),
    PROCESSING("Em processamento"),
    CALCULATING("Calculando valores"),
    CALCULATED("Valores calculados"),
    AWAITING_PAYMENT("Aguardando pagamento"),
    PAID("Pago"),
    COMPLETED("Concluído"),
    CANCELLED("Cancelado"),
    ERROR("Erro no processamento");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}