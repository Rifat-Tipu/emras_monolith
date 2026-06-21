# Emras — Clothing Store Web Application

Monolithic e-commerce platform for Emras. Backend: Spring Boot 4.1.0 (Java 21).
Frontend: React 18 + Tailwind CSS (Phase 1.1 frontend setup — next step).

## Stack

- **Backend**: Spring Boot 4.1.0, Java 21, Spring Framework 7, Spring Security 7
- **Database**: PostgreSQL 18, Flyway migrations
- **Cache**: Spring Cache + Caffeine
- **Frontend**: React 18, Vite, Tailwind CSS, Framer Motion
- **AI**: Anthropic Claude API (tool-calling shopping assistant)

## Prerequisites

- Java 21 (JDK)
- Node.js 20+
- Docker Desktop
- Maven (or use IntelliJ's bundled Maven)

## Getting Started

### 1. Start infrastructure (PostgreSQL 18 + MailHog)

```bash
docker-compose up -d
```

This starts:
- PostgreSQL 18 on `localhost:5432` (db: `emras_db`, user: `emras_user`, pass: `emras_pass`)
- MailHog on `localhost:8025` (web UI to view test emails)

### 2. Run the backend

Open `backend/` in IntelliJ IDEA, let Maven resolve dependencies, then run
`EmrasApplication.java`. It starts on **http://localhost:8080**.

Or via terminal:

```bash
cd backend
mvn spring-boot:run
```

The `local` Spring profile is active by default (see `application-local.yml`).

### 3. Verify it's running

- Health check: http://localhost:8080/actuator/health
- Swagger UI:   http://localhost:8080/swagger-ui.html

### 4. Set up Git branch structure

```bash
bash setup-git.sh
```

Creates `main` and `dev` branches. Cut `feature/*` branches from `dev`.

## Project Structure

```
emras/
├── backend/                  # Spring Boot 4.1.0 monolith
│   └── src/main/java/com/emras/
│       ├── shared/           # ApiResponse, BaseEntity, exceptions, config, constants
│       ├── user/             # Users, auth, addresses
│       ├── product/          # Product catalog
│       ├── category/         # Categories
│       ├── cart/             # Shopping cart
│       ├── order/            # Orders
│       ├── payment/          # bKash / Nagad / COD
│       └── ai/                # AI shopping assistant
├── frontend/                  # React 18 + Tailwind (scaffolded next)
├── docker-compose.yml          # PostgreSQL 18 + MailHog
├── .env.example                # Copy to .env and fill in secrets
└── setup-git.sh                # Initializes main/dev branches
```

## Notes on Spring Boot 4.1.0

This project targets the **latest Spring Boot release (4.1.0, June 2026)**, which
runs on **Spring Framework 7** and **Jakarta EE 11**, with **Java 21 as the minimum
baseline**. A few things that differ from Spring Boot 3.x:

- **Endpoints are secured by default.** `SecurityConfig` explicitly whitelists
  public routes (`/api/v1/auth/**`, `/api/v1/products/**`, Swagger, health check).
  Everything else requires authentication — JWT auth wiring comes in Phase 1.5.
- **Jackson 3** is the default JSON engine. We've pinned `springdoc-openapi` to a
  version built for Boot 4 to avoid the known Jackson 2/3 conflict.
- **CSRF is disabled** intentionally since this is a stateless JWT-based REST API
  with no browser session cookies.

## Next Steps

Phase 1.1 backend foundation is complete. Next: **Phase 1.2 — Database Schema &
Flyway Migrations**.
