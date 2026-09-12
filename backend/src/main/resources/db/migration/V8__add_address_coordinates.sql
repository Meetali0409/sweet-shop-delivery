-- Add lat/lng to addresses for precise delivery distance calculation
ALTER TABLE addresses ADD COLUMN latitude DECIMAL(10, 7);
ALTER TABLE addresses ADD COLUMN longitude DECIMAL(10, 7);
ALTER TABLE addresses ADD COLUMN address_type VARCHAR(20) DEFAULT 'HOME';
