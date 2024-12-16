-- Criar tipo enum se não existir
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'order_status') THEN
        CREATE TYPE order_status AS ENUM (
            'RECEIVED',
            'PROCESSING',
            'CALCULATING',
            'CALCULATED',
            'AWAITING_PAYMENT',
            'PAID',
            'COMPLETED',
            'CANCELLED',
            'ERROR'
        );
    END IF;
END$$;

-- Criar tabelas
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    external_order_id VARCHAR(255) NOT NULL UNIQUE,
    status order_status NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    total_price DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Criar índices se não existirem
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class c WHERE c.relname = 'idx_orders_external_order_id') THEN
        CREATE INDEX idx_orders_external_order_id ON orders(external_order_id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_class c WHERE c.relname = 'idx_orders_status') THEN
        CREATE INDEX idx_orders_status ON orders(status);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_class c WHERE c.relname = 'idx_order_items_order_id') THEN
        CREATE INDEX idx_order_items_order_id ON order_items(order_id);
    END IF;
END$$;