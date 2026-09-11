-- Add Razorpay payment tracking columns to orders
ALTER TABLE orders ADD COLUMN razorpay_order_id VARCHAR(100);
ALTER TABLE orders ADD COLUMN razorpay_payment_id VARCHAR(100);
ALTER TABLE orders ADD COLUMN razorpay_signature VARCHAR(500);

CREATE INDEX idx_orders_razorpay_order_id ON orders(razorpay_order_id);
