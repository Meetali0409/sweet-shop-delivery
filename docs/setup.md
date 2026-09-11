# Sweet Shop - Setup Guide

## Prerequisites

- **Android Studio** Arctic Fox or later (with Kotlin and Compose support)
- **JDK 21** or later
- **Docker** and Docker Compose (for database)
- **PostgreSQL 16** (if running without Docker)

## Quick Start

### 1. Start the Database

Using Docker Compose (recommended):

```bash
docker compose up -d postgres
```

Or manually install PostgreSQL and create the database:

```sql
CREATE DATABASE sweetshop;
CREATE USER sweetshop WITH PASSWORD 'sweetshop123';
GRANT ALL PRIVILEGES ON DATABASE sweetshop TO sweetshop;
```

### 2. Start the Backend

```bash
cd backend
./gradlew bootRun
```

The backend will:
- Start on `http://localhost:8080`
- Run Flyway migrations automatically
- Seed the database with sample data

Verify: `http://localhost:8080/actuator/health`

API Docs: `http://localhost:8080/swagger-ui.html`

### 3. Run the Customer App

1. Open the project in Android Studio
2. Select `customer-app` run configuration
3. Configure API URL:
   - For emulator: `http://10.0.2.2:8080/api/v1/` (default)
   - For physical device: `http://<your-ip>:8080/api/v1/`
   - Change in `customer-app/build.gradle.kts` → `buildConfigField`
4. Run on emulator or device

### 4. Run the Admin App

1. Select `admin-app` run configuration
2. Same API URL configuration as customer app
3. Run on emulator or device

### 5. Full Stack with Docker

```bash
docker compose up
```

This starts PostgreSQL, Backend, and Adminer (database UI at `http://localhost:8081`).

## Test Accounts

> **DEVELOPMENT ONLY** — Do not use these credentials in production.

### Admin
- Email: `admin@sweetshop.com`
- Password: `Admin@123`

### Customer
- Email: `riya@example.com`
- Password: `Customer@123`

- Email: `amit@example.com`
- Password: `Customer@123`

- Email: `neha@example.com`
- Password: `Customer@123`

## Configuration

### Backend Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_PORT` | 5432 | PostgreSQL port |
| `DB_NAME` | sweetshop | Database name |
| `DB_USERNAME` | sweetshop | Database user |
| `DB_PASSWORD` | sweetshop123 | Database password |
| `JWT_SECRET` | (dev default) | JWT signing secret |
| `STORAGE_TYPE` | local | File storage type |
| `SERVER_PORT` | 8080 | Server port |

### Android API URL

In `build.gradle.kts`:
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/api/v1/\"")
```

For physical devices, use your machine's local IP address.

## Project Structure

```
sweet-shop/
├── customer-app/          # Customer Android app
├── admin-app/             # Admin Android app
├── backend/               # Spring Boot backend
│   ├── src/main/kotlin/   # Kotlin source
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/  # Flyway migrations
│   └── build.gradle.kts
├── database/
│   ├── migrations/        # SQL migration files
│   └── seed/              # Seed data
├── docs/                  # Documentation
├── docker-compose.yml     # Docker configuration
├── .env.example           # Environment template
└── README.md
```
