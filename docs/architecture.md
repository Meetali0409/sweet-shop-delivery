# Sweet Shop - Architecture Document

## Overview

Sweet Shop is a full-stack e-commerce application for an Indian sweet shop, consisting of:

1. **Customer Android App** - Browse, search, order sweets
2. **Admin Android App** - Manage products, orders, customers, inventory
3. **Backend REST API** - Spring Boot service handling all business logic
4. **PostgreSQL Database** - Persistent data storage

## System Architecture

```
┌─────────────────┐    ┌─────────────────┐
│  Customer App   │    │   Admin App     │
│  (Android/      │    │  (Android/      │
│   Jetpack       │    │   Jetpack       │
│   Compose)      │    │   Compose)      │
└────────┬────────┘    └────────┬────────┘
         │                      │
         │     HTTPS/REST       │
         └──────────┬───────────┘
                    │
         ┌──────────▼───────────┐
         │   Spring Boot API    │
         │   (Kotlin)           │
         │                      │
         │  ┌────────────────┐  │
         │  │  Security      │  │
         │  │  (JWT + Roles) │  │
         │  └────────────────┘  │
         │                      │
         │  ┌────────────────┐  │
         │  │  Business      │  │
         │  │  Logic Layer   │  │
         │  └────────────────┘  │
         │                      │
         │  ┌────────────────┐  │
         │  │  Data Access   │  │
         │  │  (JPA/Hibernate│  │
         │  └────────────────┘  │
         └──────────┬───────────┘
                    │
         ┌──────────▼───────────┐
         │    PostgreSQL        │
         │    Database          │
         └──────────────────────┘
```

## Technology Stack

### Backend
| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| Framework | Spring Boot 3.4.1 |
| Security | Spring Security + JWT |
| ORM | JPA/Hibernate |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| API Docs | SpringDoc OpenAPI 2.7 |
| Build Tool | Gradle 8.12 |

### Android Apps
| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Network | Retrofit + OkHttp |
| Image Loading | Coil |
| Local Storage | Room (customer cart cache) |
| Preferences | DataStore |
| Navigation | Navigation Compose |

## Design Decisions

### 1. Separate Android Apps
Customer and Admin apps are separate modules to:
- Keep different navigation patterns (bottom nav vs drawer)
- Allow independent deployment
- Reduce APK size for each user type
- Enforce separation of concerns

### 2. JWT Authentication
- Access tokens are short-lived (15 minutes)
- Refresh tokens are long-lived (7 days)
- Stored in DataStore on Android
- AuthInterceptor handles automatic token refresh

### 3. Pessimistic Locking for Inventory
Order creation uses `@Lock(LockModeType.PESSIMISTIC_WRITE)` on product stock queries to prevent race conditions when multiple customers order simultaneously.

### 4. File Storage Abstraction
FileStorageService interface allows easy migration from local storage to cloud (S3, Azure Blob, GCS).

### 5. Payment Abstraction
PaymentService interface allows adding Razorpay/Stripe without changing order logic. Currently implements COD only.

### 6. Order Item Snapshots
Order items store product name, image, and price at time of purchase. This ensures historical order accuracy even if products change later.

## Security Model

- All passwords hashed with BCrypt
- JWT tokens for stateless authentication
- Role-based access control (CUSTOMER, ADMIN)
- Backend enforces authorization on all endpoints
- Admin endpoints require ADMIN role (not just frontend checks)
- Input validation on all API endpoints
- SQL injection prevention via parameterized JPA queries

## Data Flow

### Order Creation Flow
1. Customer selects items and proceeds to checkout
2. Frontend sends CreateOrderRequest with addressId and optional couponCode
3. Backend validates cart, address, stock availability, and pincode serviceability
4. Within a transaction:
   a. Lock product rows for update
   b. Verify stock is sufficient
   c. Calculate totals (subtotal, discount, delivery, tax)
   d. Create order and order items (snapshot product data)
   e. Decrease stock quantities
   f. Clear customer's cart
   g. Create notification
5. Return order confirmation

### Status Transition Rules
```
PLACED → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED
PLACED → CANCELLED
CONFIRMED → CANCELLED
```
