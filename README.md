# Sweet Shop

A production-ready e-commerce application for an Indian sweet shop, featuring a customer Android app, admin Android app, and Spring Boot backend.

## Features

### Customer App
- Browse and search sweets by name, category
- View detailed product information with weight options
- Add items to cart with quantity and weight selection
- Apply discount coupons
- Manage delivery addresses
- Place orders with Cash on Delivery
- Track order status in real-time
- View order history and reorder
- Wishlist management
- Product reviews and ratings
- Beautiful Material 3 UI with warm sweet-shop theme

### Admin App
- Dashboard with KPI cards and sales overview
- Product management (CRUD, stock updates, featured/bestseller toggles)
- Category management
- Order management with status workflow
- Customer management
- Coupon management
- Inventory monitoring with low-stock alerts
- Sales reports with date range filtering
- Professional Material 3 admin interface

### Backend
- RESTful API with Spring Boot
- JWT authentication with refresh tokens
- Role-based authorization (CUSTOMER, ADMIN)
- PostgreSQL database with Flyway migrations
- Pessimistic locking for inventory management
- File storage abstraction (local + cloud-ready)
- Payment service abstraction (COD + future payment gateways)
- Swagger/OpenAPI documentation
- Comprehensive error handling
- Actuator health checks

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Customer App | Kotlin, Jetpack Compose, Material 3, Hilt, Retrofit, Room, Coil |
| Admin App | Kotlin, Jetpack Compose, Material 3, Hilt, Retrofit, Coil |
| Backend | Kotlin, Spring Boot 3.4, Spring Security, JPA/Hibernate |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| API Docs | SpringDoc OpenAPI |
| Container | Docker, Docker Compose |
| CI/CD | GitHub Actions |

## Quick Start

### Prerequisites
- Android Studio (latest)
- JDK 21+
- Docker and Docker Compose

### 1. Start Database
```bash
docker compose up -d postgres
```

### 2. Start Backend
```bash
cd backend
./gradlew bootRun
```

Backend runs at `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Run Customer App
- Open project in Android Studio
- Select `customer-app` configuration
- Run on emulator (API URL defaults to `http://10.0.2.2:8080/api/v1/`)

### 4. Run Admin App
- Select `admin-app` configuration
- Run on emulator

### Full Docker Setup
```bash
docker compose up
```

## Test Credentials

> **Development use only**

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@sweetshop.com | Admin@123 |
| Customer | riya@example.com | Customer@123 |
| Customer | amit@example.com | Customer@123 |
| Customer | neha@example.com | Customer@123 |

## Project Structure

```
sweet-shop/
├── customer-app/          # Customer Android app (Jetpack Compose)
├── admin-app/             # Admin Android app (Jetpack Compose)
├── backend/               # Spring Boot REST API
│   ├── src/main/kotlin/com/sweetshop/backend/
│   │   ├── controller/    # REST controllers
│   │   ├── service/       # Business logic
│   │   ├── repository/    # Data access
│   │   ├── entity/        # JPA entities
│   │   ├── dto/           # Data transfer objects
│   │   ├── security/      # JWT + Spring Security
│   │   ├── config/        # App configuration
│   │   └── exception/     # Error handling
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/  # Flyway SQL migrations
├── docs/                  # Architecture, API, DB docs
├── .github/workflows/     # CI/CD pipelines
├── docker-compose.yml
├── .env.example
└── README.md
```

## API Documentation

See [docs/api.md](docs/api.md) for complete API reference.

Interactive Swagger UI available at `http://localhost:8080/swagger-ui.html` when backend is running.

## Architecture

See [docs/architecture.md](docs/architecture.md) for detailed architecture documentation.

## Database Schema

See [docs/database.md](docs/database.md) for complete database schema.

## Configuration

### Backend Environment Variables
Copy `.env.example` and configure:
```bash
cp .env.example .env
```

### Android API URL
For physical device testing, update `API_BASE_URL` in the app's `build.gradle.kts`:
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://YOUR_IP:8080/api/v1/\"")
```

## Deployment

### Google Cloud Run + Neon (recommended)

The backend is a stateless Docker image, so it runs as-is on [Cloud Run](https://cloud.google.com/run) with a [Neon](https://neon.tech) Postgres database. Cloud Run's free tier (2M requests, 180K vCPU-sec, 360K GiB-sec per month) doesn't expire, unlike time-limited free trials on other providers.

**One-time setup:**
1. Create a GCP project, enable billing, and enable the Cloud Run + Artifact Registry APIs.
2. Create a Neon project and copy its **pooled** connection string (the host with the `-pooler` suffix) from the Neon dashboard.
3. Copy `backend/.env.gcp.example` to `backend/.env.gcp` (gitignored) and fill in real values.

**Manual deploy:**
```bash
cd backend
export $(cat .env.gcp | xargs)   # or export each var manually
./scripts/deploy-gcp.sh
```

**CI/CD deploy:** `.github/workflows/backend-gcp-deploy.yml` runs the same steps via GitHub Actions using Workload Identity Federation (no static GCP keys stored in GitHub). It's `workflow_dispatch`-only (manual) until these repo secrets are set to real values:

| Secret | Description |
|---|---|
| `GCP_PROJECT_ID` | Target GCP project id |
| `GCP_WORKLOAD_IDENTITY_PROVIDER` | Workload Identity Federation provider resource name |
| `GCP_SERVICE_ACCOUNT` | Service account email with Cloud Run Admin, Artifact Registry Writer, Service Account User roles |
| `DATABASE_URL` | Neon pooled JDBC URL, e.g. `jdbc:postgresql://ep-xxxx-pooler.<region>.aws.neon.tech/sweetshop?sslmode=require&prepareThreshold=0` |
| `DB_USERNAME`, `DB_PASSWORD` | Neon database credentials |
| `JWT_SECRET` | 256-bit+ random secret |
| `ALLOWED_ORIGINS`, `STORAGE_TYPE`, `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET` | Same as `.env.example` |

Once set, add a `push` trigger to the workflow for auto-deploy on merge to `main`.

After deploying, update `API_BASE_URL` in `admin-app/build.gradle.kts` and `customer-app/build.gradle.kts` (and the `API_BASE_URL` GitHub secret used for release builds) to the new Cloud Run service URL.

### Railway

The repo also includes `backend/railway.toml` for deploying to Railway (the previous hosting provider) via its GitHub auto-deploy integration — no extra setup beyond linking the repo in the Railway dashboard.

## Seed Data

The database is seeded with:
- 10 categories (Traditional, Bestseller, Bengali Sweets, etc.)
- 20 products (Gulab Jamun, Kaju Katli, Rasgulla, etc.)
- Weight options for all products (250g, 500g, 1kg)
- 4 discount coupons
- Sample orders and customers
- Serviceable pincodes

## Future Enhancements

The architecture supports future additions:
- Online payments (Razorpay/Stripe)
- Push notifications (Firebase Cloud Messaging)
- Social login (Google/Apple)
- Elasticsearch for advanced search
- Cloud file storage (S3/Azure Blob/GCS)
- Multi-language support
- Delivery tracking
- Loyalty points and referral system
- PDF invoice generation

## License

This project is for educational and demonstration purposes.
