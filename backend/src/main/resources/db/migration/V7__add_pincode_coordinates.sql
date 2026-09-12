-- Add coordinates to serviceable pincodes for distance-based delivery
ALTER TABLE serviceable_pincodes ADD COLUMN latitude DECIMAL(10, 7);
ALTER TABLE serviceable_pincodes ADD COLUMN longitude DECIMAL(10, 7);

-- Update existing pincodes with approximate center coordinates
UPDATE serviceable_pincodes SET latitude = 18.9388, longitude = 72.8354 WHERE pincode = '400001';
UPDATE serviceable_pincodes SET latitude = 18.9533, longitude = 72.8327 WHERE pincode = '400002';
UPDATE serviceable_pincodes SET latitude = 18.9568, longitude = 72.8337 WHERE pincode = '400003';
UPDATE serviceable_pincodes SET latitude = 28.6328, longitude = 77.2197 WHERE pincode = '110001';
UPDATE serviceable_pincodes SET latitude = 28.6372, longitude = 77.2295 WHERE pincode = '110002';
UPDATE serviceable_pincodes SET latitude = 23.0225, longitude = 72.5714 WHERE pincode = '380001';
UPDATE serviceable_pincodes SET latitude = 12.9716, longitude = 77.5946 WHERE pincode = '560001';
UPDATE serviceable_pincodes SET latitude = 13.0827, longitude = 80.2707 WHERE pincode = '600001';
UPDATE serviceable_pincodes SET latitude = 22.5726, longitude = 88.3639 WHERE pincode = '700001';
UPDATE serviceable_pincodes SET latitude = 17.3850, longitude = 78.4867 WHERE pincode = '500001';
