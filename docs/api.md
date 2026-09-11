# Sweet Shop API Documentation

Base URL: `http://localhost:8080/api/v1`

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Authentication

All authenticated endpoints require:
```
Authorization: Bearer <access_token>
```

### POST /auth/register
Register a new customer account.

**Request:**
```json
{
  "name": "Riya Sharma",
  "email": "riya@example.com",
  "phone": "9876543211",
  "password": "Password@123",
  "confirmPassword": "Password@123"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci...",
    "user": {
      "id": 1,
      "name": "Riya Sharma",
      "email": "riya@example.com",
      "phone": "9876543211",
      "role": "CUSTOMER"
    }
  }
}
```

### POST /auth/login
```json
{
  "email": "riya@example.com",
  "password": "Password@123"
}
```

### POST /auth/refresh
```json
{
  "refreshToken": "eyJhbGci..."
}
```

## Products

### GET /products
Query params: `page`, `size`, `category` (ID), `search`, `sort` (price_asc, price_desc, rating, newest)

### GET /products/{id}
### GET /products/featured
### GET /products/bestsellers
### GET /products/{id}/reviews
### POST /products/{id}/reviews (authenticated)

## Categories

### GET /categories

## Cart (authenticated)

### GET /cart
### POST /cart/items
```json
{
  "productId": 1,
  "quantity": 2,
  "selectedWeight": "500g"
}
```
### PUT /cart/items/{id}
### DELETE /cart/items/{id}
### DELETE /cart

## Addresses (authenticated)

### GET /addresses
### POST /addresses
### PUT /addresses/{id}
### DELETE /addresses/{id}

## Orders (authenticated)

### POST /orders
```json
{
  "addressId": 1,
  "paymentMethod": "COD",
  "couponCode": "WELCOME20",
  "notes": "Please deliver before 6 PM"
}
```
### GET /orders
### GET /orders/{id}
### POST /orders/{id}/cancel
### POST /orders/{id}/reorder

## Wishlist (authenticated)

### GET /wishlist
### POST /wishlist/{productId}
### DELETE /wishlist/{productId}

## Coupons (authenticated)

### GET /coupons
### POST /coupons/validate

## Notifications (authenticated)

### GET /notifications
### GET /notifications/unread-count
### PUT /notifications/{id}/read

## Admin Endpoints (ADMIN role required)

### Products
- POST /admin/products
- PUT /admin/products/{id}
- DELETE /admin/products/{id}
- PUT /admin/products/{id}/stock

### Categories
- POST /admin/categories
- PUT /admin/categories/{id}
- DELETE /admin/categories/{id}

### Orders
- GET /admin/orders
- GET /admin/orders/{id}
- PATCH /admin/orders/{id}/status

### Customers
- GET /admin/customers
- GET /admin/customers/{id}
- PATCH /admin/customers/{id}/toggle-status

### Coupons
- GET /admin/coupons
- POST /admin/coupons
- PUT /admin/coupons/{id}
- DELETE /admin/coupons/{id}

### Inventory
- GET /admin/inventory
- PUT /admin/inventory/{productId}/stock

### Reports & Dashboard
- GET /admin/dashboard
- GET /admin/reports/sales
- GET /admin/reports/sales-overview

## Error Response Format
```json
{
  "success": false,
  "code": "PRODUCT_OUT_OF_STOCK",
  "message": "This product is currently out of stock.",
  "timestamp": "2026-08-24T10:30:00Z"
}
```

## Common Error Codes
- `VALIDATION_ERROR` - Input validation failed
- `RESOURCE_NOT_FOUND` - Entity not found
- `UNAUTHORIZED` - Authentication required
- `FORBIDDEN` - Insufficient permissions
- `PRODUCT_OUT_OF_STOCK` - Product stock depleted
- `INVALID_COUPON` - Coupon validation failed
- `INVALID_STATUS_TRANSITION` - Invalid order status change
- `PINCODE_NOT_SERVICEABLE` - Delivery pincode not supported
