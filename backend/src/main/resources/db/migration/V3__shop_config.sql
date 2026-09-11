-- Shop configuration for white-label support
CREATE TABLE shop_config (
    id BIGSERIAL PRIMARY KEY,
    shop_name VARCHAR(200) NOT NULL DEFAULT 'Sweet Shop',
    tagline VARCHAR(500) DEFAULT 'Delicious sweets delivered to your door',
    logo_url VARCHAR(500),
    primary_color VARCHAR(7) DEFAULT '#E91E63',
    secondary_color VARCHAR(7) DEFAULT '#FF5722',
    currency_code VARCHAR(3) NOT NULL DEFAULT 'INR',
    currency_symbol VARCHAR(5) NOT NULL DEFAULT '₹',
    country_code VARCHAR(3) NOT NULL DEFAULT 'IN',
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 5.00,
    support_email VARCHAR(255),
    support_phone VARCHAR(20),
    terms_url VARCHAR(500),
    privacy_url VARCHAR(500),
    about_text TEXT,
    payment_methods VARCHAR(100) NOT NULL DEFAULT 'COD,ONLINE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed default config
INSERT INTO shop_config (shop_name, tagline, currency_code, currency_symbol, country_code, tax_rate, payment_methods)
VALUES ('Sweet Shop', 'Delicious sweets delivered to your door', 'INR', '₹', 'IN', 5.00, 'COD,ONLINE');
