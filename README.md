# Project KEYSTONE — Field Service Management Platform

A professional Java full-stack field service management platform based on the Zidio Development Project KEYSTONE brief.

## Stack
- Java 21
- Spring Boot 3.5.x
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- React + TypeScript + Vite
- Swagger / OpenAPI
- Docker Compose

## Roles
- Manager/Admin
- Dispatcher
- Technician
- Customer

## Core features
Authentication/RBAC, customers/sites, work orders, governed lifecycle + audit history, dispatch, technician field view, parts and time logging, SLA monitoring, dashboard/reporting, customer portal, notifications, validation, pagination/filtering, Swagger.

## Seed logins
All seed users use password: `password`

| Role | Email |
|---|---|
| Manager | manager@keystone.local |
| Dispatcher | dispatcher@keystone.local |
| Technician | technician@keystone.local |
| Customer | customer@keystone.local |

## Local setup

### 1. Requirements
Install:
- JDK 21
- Maven 3.9+
- Node.js 20+
- PostgreSQL 16+
- Git
- Docker Desktop (optional)

### 2. Database
Create a database named `keystone`.

Windows:
```sql
CREATE DATABASE keystone;
```

Set environment variables:
```text
DB_URL=jdbc:postgresql://localhost:5432/keystone
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password
JWT_SECRET=change-this-to-a-long-random-secret-at-least-32-characters
```

### 3. Backend
```bash
cd backend
mvn spring-boot:run
```

Backend:
`http://localhost:8080`

Swagger:
`http://localhost:8080/swagger-ui.html`

### 4. Frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend:
`http://localhost:5173`

### 5. Docker
From the project root:
```bash
docker compose up --build
```

## Work-order lifecycle

NEW -> ASSIGNED -> IN_PROGRESS -> ON_HOLD -> IN_PROGRESS -> COMPLETED -> CLOSED

NEW -> CANCELLED
ASSIGNED -> CANCELLED
IN_PROGRESS -> CANCELLED
ON_HOLD -> CANCELLED

Terminal states: CLOSED, CANCELLED.

The backend, not the React UI, enforces lifecycle and role rules.

## Important acceptance/security points
- DTOs are used at API boundaries.
- Controllers are thin.
- Business rules live in services.
- Status history is append-only.
- Parts and stock decrement are transactional.
- Stock cannot become negative.
- Customer access is organisation-scoped.
- Technicians can act only on their assigned jobs.
- Managers can close jobs.
- BCrypt passwords and expiring JWTs.
- Consistent validation/error responses.
- Paginated work-order lists.
- SLA due dates and scheduled breach checks.
- OpenAPI documentation.

## Demo flow
1. Login as manager.
2. Open dashboard.
3. Create customer/site.
4. Create work order.
5. Assign technician.
6. Login as technician and start/hold/complete.
7. Add parts and time.
8. Login as manager and close.
9. Login as customer and verify only customer-owned requests are visible.

## Repository layout
```text
keystone/
  backend/
    src/main/java/com/keystone/...
    src/main/resources/db/migration/
  frontend/
    src/
  docker-compose.yml
  README.md
```
