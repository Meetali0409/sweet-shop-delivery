# Sweet Shop - Database Schema

## Entity Relationship Diagram

```
┌──────────┐     ┌──────────┐     ┌───────────┐
│  users   │1───*│addresses │     │categories │
└────┬─────┘     └──────────┘     └─────┬─────┘
     │                                   │
     │1                                  │1
     │                                   │
     ├──*┌──────────┐              *┌────┴─────┐
     │   │  carts   │1──*┌─────────┤ products  │1──*┌──────────────┐
     │   └──────────┘    │cart_items└─────┬─────┘   │product_weights│
     │                   └─────────┘     │          └──────────────┘
     │                                   │
     │1                                  │*
     │                                   │
     ├──*┌──────────┐    ┌───────────┐*──┤
     │   │  orders  │1──*│order_items│   │
     │   └────┬─────┘    └───────────┘   │
     │        │                          │
     │        │                          │
     ├──*┌────┴──────┐                   │
     │   │ reviews   │*──────────────────┘
     │   └───────────┘
     │
     ├──*┌───────────┐
     │   │ wishlist  │*──products
     │   └───────────┘
     │
     ├──*┌───────────────┐
     │   │notifications  │
     │   └───────────────┘
     │
     └──*┌───────────────┐
         │refresh_tokens │
         └───────────────┘

┌──────────┐  ┌─────────────────┐  ┌─────────────────┐
│ coupons  │  │ delivery_config │  │serviceable_     │
│          │  │                 │  │pincodes         │
└──────────┘  └─────────────────┘  └─────────────────┘
```

## Tables

### users
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(255) | NOT NULL, UNIQUE |
| phone | VARCHAR(20) | |
| password_hash | VARCHAR(255) | NOT NULL |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'CUSTOMER' |
| profile_image | VARCHAR(500) | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### addresses
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| user_id | BIGINT | NOT NULL, FK -> users |
| name | VARCHAR(100) | NOT NULL |
| phone | VARCHAR(20) | NOT NULL |
| address_line1 | VARCHAR(255) | NOT NULL |
| address_line2 | VARCHAR(255) | |
| city | VARCHAR(100) | NOT NULL |
| state | VARCHAR(100) | NOT NULL |
| pincode | VARCHAR(10) | NOT NULL |
| landmark | VARCHAR(255) | |
| is_default | BOOLEAN | NOT NULL, DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### categories
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| name | VARCHAR(100) | NOT NULL, UNIQUE |
| description | VARCHAR(500) | |
| image | VARCHAR(500) | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE |
| sort_order | INT | NOT NULL, DEFAULT 0 |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### products
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| name | VARCHAR(200) | NOT NULL |
| description | TEXT | |
| category_id | BIGINT | NOT NULL, FK -> categories |
| image_url | VARCHAR(500) | |
| price | DECIMAL(10,2) | NOT NULL |
| discount_price | DECIMAL(10,2) | |
| unit | VARCHAR(50) | NOT NULL, DEFAULT '500g' |
| stock_quantity | INT | NOT NULL, DEFAULT 0 |
| minimum_order_quantity | INT | NOT NULL, DEFAULT 1 |
| is_available | BOOLEAN | NOT NULL, DEFAULT TRUE |
| is_featured | BOOLEAN | NOT NULL, DEFAULT FALSE |
| is_bestseller | BOOLEAN | NOT NULL, DEFAULT FALSE |
| rating | DECIMAL(2,1) | NOT NULL, DEFAULT 0.0 |
| total_reviews | INT | NOT NULL, DEFAULT 0 |
| ingredients | TEXT | |
| allergen_info | TEXT | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### product_weights
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| product_id | BIGINT | NOT NULL, FK -> products |
| weight | VARCHAR(50) | NOT NULL |
| price | DECIMAL(10,2) | NOT NULL |
| discount_price | DECIMAL(10,2) | |

### orders
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| order_number | VARCHAR(20) | NOT NULL, UNIQUE |
| user_id | BIGINT | NOT NULL, FK -> users |
| address_id | BIGINT | FK -> addresses |
| subtotal | DECIMAL(10,2) | NOT NULL |
| discount | DECIMAL(10,2) | NOT NULL, DEFAULT 0 |
| delivery_charge | DECIMAL(10,2) | NOT NULL, DEFAULT 0 |
| tax | DECIMAL(10,2) | NOT NULL, DEFAULT 0 |
| total_amount | DECIMAL(10,2) | NOT NULL |
| payment_method | VARCHAR(30) | NOT NULL, DEFAULT 'COD' |
| payment_status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' |
| order_status | VARCHAR(30) | NOT NULL, DEFAULT 'PLACED' |
| coupon_code | VARCHAR(50) | |
| notes | TEXT | |
| estimated_delivery | DATE | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### order_items
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| order_id | BIGINT | NOT NULL, FK -> orders |
| product_id | BIGINT | NOT NULL, FK -> products |
| product_name | VARCHAR(200) | NOT NULL |
| product_image | VARCHAR(500) | |
| quantity | INT | NOT NULL |
| selected_weight | VARCHAR(50) | NOT NULL |
| unit_price | DECIMAL(10,2) | NOT NULL |
| total_price | DECIMAL(10,2) | NOT NULL |

### coupons
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| coupon_code | VARCHAR(50) | NOT NULL, UNIQUE |
| description | VARCHAR(500) | |
| discount_type | VARCHAR(20) | NOT NULL |
| discount_value | DECIMAL(10,2) | NOT NULL |
| minimum_order_value | DECIMAL(10,2) | NOT NULL, DEFAULT 0 |
| maximum_discount | DECIMAL(10,2) | |
| valid_from | TIMESTAMP | NOT NULL |
| valid_until | TIMESTAMP | NOT NULL |
| usage_limit | INT | |
| usage_count | INT | NOT NULL, DEFAULT 0 |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### reviews
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| user_id | BIGINT | NOT NULL, FK -> users |
| product_id | BIGINT | NOT NULL, FK -> products |
| order_id | BIGINT | NOT NULL, FK -> orders |
| rating | INT | NOT NULL, CHECK 1-5 |
| review | TEXT | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |
| | | UNIQUE(user_id, product_id, order_id) |

### wishlist
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| user_id | BIGINT | NOT NULL, FK -> users |
| product_id | BIGINT | NOT NULL, FK -> products |
| created_at | TIMESTAMP | NOT NULL |
| | | UNIQUE(user_id, product_id) |

### notifications
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| user_id | BIGINT | NOT NULL, FK -> users |
| title | VARCHAR(200) | NOT NULL |
| message | TEXT | NOT NULL |
| type | VARCHAR(50) | NOT NULL |
| is_read | BOOLEAN | NOT NULL, DEFAULT FALSE |
| reference_id | BIGINT | |
| created_at | TIMESTAMP | NOT NULL |

### delivery_config
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| delivery_charge | DECIMAL(10,2) | NOT NULL, DEFAULT 40.00 |
| free_delivery_threshold | DECIMAL(10,2) | NOT NULL, DEFAULT 500.00 |
| estimated_delivery_days | INT | NOT NULL, DEFAULT 3 |
| updated_at | TIMESTAMP | NOT NULL |

### serviceable_pincodes
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| pincode | VARCHAR(10) | NOT NULL, UNIQUE |
| city | VARCHAR(100) | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE |

### refresh_tokens
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGSERIAL | PRIMARY KEY |
| user_id | BIGINT | NOT NULL, FK -> users |
| token | VARCHAR(500) | NOT NULL, UNIQUE |
| expires_at | TIMESTAMP | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |
