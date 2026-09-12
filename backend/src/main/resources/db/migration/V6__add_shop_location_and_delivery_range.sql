-- Add shop location and distance-based delivery settings
ALTER TABLE delivery_config ADD COLUMN shop_latitude DECIMAL(10, 7);
ALTER TABLE delivery_config ADD COLUMN shop_longitude DECIMAL(10, 7);
ALTER TABLE delivery_config ADD COLUMN delivery_radius_km DECIMAL(10, 2) DEFAULT 15.00;
ALTER TABLE delivery_config ADD COLUMN per_km_charge DECIMAL(10, 2) DEFAULT 5.00;
ALTER TABLE delivery_config ADD COLUMN base_delivery_distance_km DECIMAL(10, 2) DEFAULT 3.00;

-- Set default location (Mumbai)
UPDATE delivery_config SET shop_latitude = 19.0760, shop_longitude = 72.8777;
