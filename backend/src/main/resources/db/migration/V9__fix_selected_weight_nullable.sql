ALTER TABLE cart_items ALTER COLUMN selected_weight DROP NOT NULL;
ALTER TABLE cart_items ALTER COLUMN selected_weight DROP DEFAULT;
ALTER TABLE order_items ALTER COLUMN selected_weight DROP NOT NULL;
