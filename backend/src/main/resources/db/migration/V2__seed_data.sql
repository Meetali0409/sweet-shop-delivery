-- Seed admin user (password: Admin@123)
INSERT INTO users (name, email, phone, password_hash, role, is_active) VALUES
('Admin User', 'admin@sweetshop.com', '9876543210', '$2a$10$iKpXnxVX6Ip.vckCtHBnpumXGAdcD30Sb7PD6HDDWuG2l/8q1UQXy', 'ADMIN', true);

-- Seed customer user (password: Customer@123)
INSERT INTO users (name, email, phone, password_hash, role, is_active) VALUES
('Riya Sharma', 'riya@example.com', '9876543211', '$2a$10$k3eYoYApzX6zbcoxLBmqdeh/vlVJLDAQcFwin5qXuA2in1qmtQKmq', 'CUSTOMER', true),
('Amit Verma', 'amit@example.com', '9876543212', '$2a$10$k3eYoYApzX6zbcoxLBmqdeh/vlVJLDAQcFwin5qXuA2in1qmtQKmq', 'CUSTOMER', true),
('Neha Patel', 'neha@example.com', '9876543213', '$2a$10$k3eYoYApzX6zbcoxLBmqdeh/vlVJLDAQcFwin5qXuA2in1qmtQKmq', 'CUSTOMER', true);

-- Seed addresses
INSERT INTO addresses (user_id, name, phone, address_line1, address_line2, city, state, pincode, landmark, is_default) VALUES
(2, 'Riya Sharma', '9876543211', '2718 Baker Street', 'Apt 4B', 'Mumbai', 'Maharashtra', '400001', 'Near Central Mall', true),
(3, 'Amit Verma', '9876543212', '45 MG Road', 'Floor 2', 'Delhi', 'Delhi', '110001', 'Near Metro Station', true),
(4, 'Neha Patel', '9876543213', '12 Park Avenue', NULL, 'Ahmedabad', 'Gujarat', '380001', 'Near City Garden', true);

-- Seed categories
INSERT INTO categories (name, description, image, is_active, sort_order) VALUES
('Traditional', 'Traditional Indian sweets made with authentic recipes', NULL, true, 1),
('Bestseller', 'Our most loved sweets', NULL, true, 2),
('New Arrivals', 'Fresh new additions to our menu', NULL, true, 3),
('Dry Fruits', 'Premium dry fruit based sweets', NULL, true, 4),
('Bengali Sweets', 'Authentic Bengali sweet delicacies', NULL, true, 5),
('Milk Sweets', 'Rich and creamy milk-based sweets', NULL, true, 6),
('Namkeen', 'Savory snacks and namkeen', NULL, true, 7),
('Festive Special', 'Special sweets for festivals', NULL, true, 8),
('Sugar Free', 'Healthy sugar-free alternatives', NULL, true, 9),
('Gift Boxes', 'Curated sweet gift hampers', NULL, true, 10);

-- Seed products
INSERT INTO products (name, description, category_id, image_url, price, discount_price, unit, stock_quantity, minimum_order_quantity, is_available, is_featured, is_bestseller, rating, total_reviews, ingredients, allergen_info) VALUES
('Gulab Jamun', 'Soft, round dumplings made of milk solids, soaked in sugar syrup flavored with cardamom and rose water.', 1, NULL, 240.00, NULL, '500g', 100, 1, true, true, true, 4.5, 128, 'Khoya, Sugar, Cardamom, Rose Water, Ghee', 'Contains dairy'),
('Kaju Katli', 'Premium cashew fudge with a delicate silver leaf topping. Made from finest cashews.', 4, NULL, 600.00, 560.00, '500g', 80, 1, true, true, true, 4.7, 95, 'Cashew Nuts, Sugar, Ghee, Silver Leaf', 'Contains nuts, dairy'),
('Rasgulla', 'Spongy white balls made of chhena, dipped in light sugar syrup.', 5, NULL, 200.00, NULL, '500g', 120, 1, true, true, true, 4.6, 110, 'Chhena, Sugar, Water, Cardamom', 'Contains dairy'),
('Motichoor Laddu', 'Fine boondi pearls shaped into perfect round laddus with aromatic flavoring.', 1, NULL, 320.00, NULL, '500g', 90, 1, true, true, true, 4.4, 76, 'Besan, Sugar, Ghee, Cardamom, Saffron', 'Contains dairy, gluten'),
('Besan Laddu', 'Traditional gram flour laddus with rich ghee and cardamom flavor.', 1, NULL, 280.00, 260.00, '500g', 70, 1, true, false, true, 4.3, 65, 'Besan, Ghee, Sugar, Cardamom', 'Contains dairy, gluten'),
('Peda', 'Classic condensed milk sweets with pistachio and saffron.', 6, NULL, 350.00, NULL, '500g', 60, 1, true, true, false, 4.2, 52, 'Khoya, Sugar, Pistachio, Saffron', 'Contains dairy, nuts'),
('Badam Halwa', 'Rich almond-based halwa slow-cooked to perfection with ghee and saffron.', 4, NULL, 450.00, 420.00, '500g', 40, 1, true, false, false, 4.5, 38, 'Almonds, Sugar, Ghee, Saffron, Cardamom', 'Contains nuts, dairy'),
('Jalebi', 'Crispy, golden spirals soaked in saffron-infused sugar syrup.', 1, NULL, 180.00, NULL, '500g', 150, 1, true, false, true, 4.1, 89, 'Maida, Sugar, Saffron, Ghee', 'Contains gluten, dairy'),
('Soan Papdi', 'Flaky, melt-in-mouth sweet with layers of crispy texture.', 1, NULL, 220.00, 200.00, '500g', 100, 1, true, false, false, 4.0, 45, 'Gram Flour, Sugar, Ghee, Cardamom, Pistachio', 'Contains nuts, dairy, gluten'),
('Milk Cake', 'Dense, caramelized milk cake with a rich milky flavor.', 6, NULL, 300.00, NULL, '500g', 55, 1, true, false, false, 4.3, 34, 'Milk, Sugar, Cardamom', 'Contains dairy'),
('Ghewar', 'Traditional Rajasthani disc-shaped sweet soaked in sugar syrup.', 8, NULL, 400.00, 380.00, '500g', 30, 1, true, true, false, 4.4, 28, 'Maida, Ghee, Sugar, Milk, Saffron', 'Contains gluten, dairy'),
('Mysore Pak', 'Melt-in-mouth sweet from Mysore made with generous amounts of ghee and gram flour.', 1, NULL, 350.00, NULL, '500g', 45, 1, true, false, true, 4.6, 56, 'Besan, Ghee, Sugar', 'Contains dairy, gluten'),
('Balushahi', 'Flaky, deep-fried pastry soaked in sugar syrup, similar to a doughnut.', 1, NULL, 250.00, NULL, '500g', 65, 1, true, false, false, 4.2, 41, 'Maida, Ghee, Sugar, Yogurt', 'Contains gluten, dairy'),
('Imarti', 'Orange-red flower-shaped sweet made of urad dal batter, deep-fried and soaked in syrup.', 1, NULL, 220.00, NULL, '500g', 50, 1, true, false, false, 4.3, 33, 'Urad Dal, Sugar, Saffron, Ghee', 'Contains dairy'),
('Rasmalai', 'Soft chhena discs soaked in thickened sweetened milk with saffron and cardamom.', 5, NULL, 380.00, 350.00, '500g', 40, 1, true, true, true, 4.8, 120, 'Chhena, Milk, Sugar, Saffron, Cardamom, Pistachio', 'Contains dairy, nuts'),
('Cham Cham', 'Oblong-shaped Bengali sweet made from chhena, soaked in flavored syrup.', 5, NULL, 280.00, NULL, '500g', 35, 1, true, false, false, 4.4, 29, 'Chhena, Sugar, Cardamom', 'Contains dairy'),
('Kalakand', 'Soft milk cake with a grainy texture, garnished with pistachios.', 6, NULL, 320.00, 300.00, '500g', 50, 1, true, false, false, 4.3, 42, 'Milk, Sugar, Paneer, Pistachio', 'Contains dairy, nuts'),
('Boondi Laddu', 'Sweet round balls made from tiny fried gram flour drops bound with sugar syrup.', 1, NULL, 260.00, NULL, '500g', 80, 1, true, false, false, 4.1, 55, 'Besan, Sugar, Ghee, Cardamom', 'Contains dairy, gluten'),
('Dry Fruit Laddu', 'Nutrient-rich laddus packed with almonds, cashews, pistachios, and dates.', 4, NULL, 550.00, 520.00, '500g', 35, 1, true, true, false, 4.7, 48, 'Almonds, Cashews, Pistachios, Dates, Honey', 'Contains nuts'),
('Coconut Barfi', 'Sweet coconut fudge with a smooth texture and subtle cardamom flavor.', 1, NULL, 240.00, NULL, '500g', 70, 1, true, false, false, 4.2, 37, 'Coconut, Sugar, Cardamom, Ghee', 'Contains dairy');

-- Seed product weights
INSERT INTO product_weights (product_id, weight, price, discount_price) VALUES
(1, '250g', 120.00, NULL), (1, '500g', 240.00, NULL), (1, '1kg', 460.00, NULL),
(2, '250g', 300.00, 280.00), (2, '500g', 600.00, 560.00), (2, '1kg', 1150.00, 1080.00),
(3, '250g', 100.00, NULL), (3, '500g', 200.00, NULL), (3, '1kg', 380.00, NULL),
(4, '250g', 160.00, NULL), (4, '500g', 320.00, NULL), (4, '1kg', 620.00, NULL),
(5, '250g', 140.00, 130.00), (5, '500g', 280.00, 260.00), (5, '1kg', 540.00, 500.00),
(6, '250g', 175.00, NULL), (6, '500g', 350.00, NULL), (6, '1kg', 680.00, NULL),
(7, '250g', 225.00, 210.00), (7, '500g', 450.00, 420.00), (7, '1kg', 870.00, 810.00),
(8, '250g', 90.00, NULL), (8, '500g', 180.00, NULL), (8, '1kg', 340.00, NULL),
(9, '250g', 110.00, 100.00), (9, '500g', 220.00, 200.00), (9, '1kg', 420.00, 380.00),
(10, '250g', 150.00, NULL), (10, '500g', 300.00, NULL), (10, '1kg', 580.00, NULL),
(11, '250g', 200.00, 190.00), (11, '500g', 400.00, 380.00), (11, '1kg', 780.00, 740.00),
(12, '250g', 175.00, NULL), (12, '500g', 350.00, NULL), (12, '1kg', 680.00, NULL),
(13, '250g', 125.00, NULL), (13, '500g', 250.00, NULL), (13, '1kg', 480.00, NULL),
(14, '250g', 110.00, NULL), (14, '500g', 220.00, NULL), (14, '1kg', 420.00, NULL),
(15, '250g', 190.00, 175.00), (15, '500g', 380.00, 350.00), (15, '1kg', 740.00, 680.00),
(16, '250g', 140.00, NULL), (16, '500g', 280.00, NULL), (16, '1kg', 540.00, NULL),
(17, '250g', 160.00, 150.00), (17, '500g', 320.00, 300.00), (17, '1kg', 620.00, 580.00),
(18, '250g', 130.00, NULL), (18, '500g', 260.00, NULL), (18, '1kg', 500.00, NULL),
(19, '250g', 275.00, 260.00), (19, '500g', 550.00, 520.00), (19, '1kg', 1060.00, 1000.00),
(20, '250g', 120.00, NULL), (20, '500g', 240.00, NULL), (20, '1kg', 460.00, NULL);

-- Seed coupons
INSERT INTO coupons (coupon_code, description, discount_type, discount_value, minimum_order_value, maximum_discount, valid_from, valid_until, usage_limit, is_active) VALUES
('WELCOME20', 'Get 20% off on your first order', 'PERCENTAGE', 20.00, 200.00, 100.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1000, true),
('SWEET50', 'Flat ₹50 off on orders above ₹300', 'FIXED', 50.00, 300.00, NULL, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 500, true),
('FESTIVE15', 'Festive season 15% discount', 'PERCENTAGE', 15.00, 500.00, 150.00, '2026-08-01 00:00:00', '2026-11-30 23:59:59', 2000, true),
('DRYFRUITS10', '10% off on dry fruit sweets', 'PERCENTAGE', 10.00, 400.00, 80.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 300, true);

-- Seed delivery config
INSERT INTO delivery_config (delivery_charge, free_delivery_threshold, estimated_delivery_days) VALUES
(40.00, 500.00, 3);

-- Seed serviceable pincodes
INSERT INTO serviceable_pincodes (pincode, city, is_active) VALUES
('400001', 'Mumbai', true),
('400002', 'Mumbai', true),
('400003', 'Mumbai', true),
('110001', 'Delhi', true),
('110002', 'Delhi', true),
('380001', 'Ahmedabad', true),
('560001', 'Bangalore', true),
('600001', 'Chennai', true),
('700001', 'Kolkata', true),
('500001', 'Hyderabad', true);

-- Seed sample orders for demo
INSERT INTO orders (order_number, user_id, address_id, subtotal, discount, delivery_charge, tax, total_amount, payment_method, payment_status, order_status, created_at) VALUES
('ORD-12345', 2, 1, 1120.00, 0, 40.00, 0, 1160.00, 'COD', 'COD', 'DELIVERED', '2026-05-20 10:30:00'),
('ORD-12344', 3, 2, 600.00, 0, 40.00, 0, 640.00, 'COD', 'COD', 'PREPARING', '2026-05-20 09:15:00'),
('ORD-12343', 4, 3, 380.00, 0, 40.00, 0, 420.00, 'COD', 'COD', 'DELIVERED', '2026-05-22 08:40:00'),
('ORD-12342', 3, 2, 500.00, 0, 40.00, 0, 540.00, 'COD', 'COD', 'CANCELLED', '2026-05-01 07:30:00');

INSERT INTO order_items (order_id, product_id, product_name, product_image, quantity, selected_weight, unit_price, total_price) VALUES
(1, 1, 'Gulab Jamun', NULL, 2, '500g', 240.00, 480.00),
(1, 2, 'Kaju Katli', NULL, 1, '500g', 560.00, 560.00),
(1, 4, 'Motichoor Laddu', NULL, 1, '250g', 160.00, 160.00),
(2, 2, 'Kaju Katli', NULL, 1, '500g', 560.00, 560.00),
(2, 8, 'Jalebi', NULL, 1, '250g', 90.00, 90.00),
(3, 15, 'Rasmalai', NULL, 1, '500g', 350.00, 350.00),
(3, 3, 'Rasgulla', NULL, 1, '250g', 100.00, 100.00),
(4, 2, 'Kaju Katli', NULL, 1, '500g', 560.00, 560.00);
