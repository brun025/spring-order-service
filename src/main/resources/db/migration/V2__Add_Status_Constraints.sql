-- Migrar dados existentes (caso existam)
ALTER TABLE orders ALTER COLUMN status TYPE order_status USING status::order_status;

-- Adicionar constraints de validação
ALTER TABLE order_items ADD CONSTRAINT check_quantity_positive CHECK (quantity > 0);
ALTER TABLE order_items ADD CONSTRAINT check_unit_price_positive CHECK (unit_price > 0);
ALTER TABLE order_items ADD CONSTRAINT check_total_price_positive CHECK (total_price > 0);
ALTER TABLE orders ADD CONSTRAINT check_total_amount_positive CHECK (total_amount >= 0);