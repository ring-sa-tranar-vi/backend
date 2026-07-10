## Overview

Describe the purpose of the backend.

Example:

The backend provides REST APIs for managing users, orders, and inventory. It handles authentication, business logic, database access, and integrations with external services.

---

## Architecture

Describe the overall architecture.

Example:

```
Client
   │
   ▼
API Gateway
   │
   ▼
Controllers
   │
   ▼
Services
   │
   ▼
Repositories
   │
   ▼
Database
```

Explain each layer and its responsibility.

---

## Technology Stack

| Technology       | Purpose           |
| ---------------- | ----------------- |
| Java Spring Boot | Backend framework |
| PostgreSQL       | Database          |
| Redis            | Caching           |
| Docker           | Containerization  |
| JWT              | Authentication    |
| JUnit            | Testing           |
| Swagger/OpenAPI  | API documentation |

---

## Getting Started

### Prerequisites

- Java 21
- Gradle Wrapper (`./gradlew`)
- PostgreSQL
- Docker (optional)

### Installation

```bash
git clone ...
cd backend
```

### Install dependencies

```bash
./gradlew build
```

### Run locally

```bash
./gradlew bootRun
```

### Run all tests

```bash
./gradlew test
```

If you only want to compile the tests without executing them:

```bash
./gradlew testClasses
```

---

## Configuration

Explain configuration files.

Example:

```
application.yml
application-dev.yml
application-prod.yml
```

Environment variables:

| Variable   | Description         |
| ---------- | ------------------- |
| DB_URL     | Database connection |
| DB_USER    | Database username   |
| JWT_SECRET | JWT signing key     |
| REDIS_HOST | Redis server        |

---

## Project Structure

```
src/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── mapper/
├── config/
├── security/
├── exception/
├── util/
└── test/
```

Describe what belongs in each package.

---

## API

Document available endpoints.

### Authentication

POST /auth/login

Request

```json
{
  "email": "...",
  "password": "..."
}
```

Response

```json
{
  "token": "..."
}
```

Repeat for important endpoints.

---

## Authentication & Authorization

Explain:

- JWT
- OAuth
- Roles
- Permissions
- Refresh tokens

Example flow:

```
Login
   ↓
JWT issued
   ↓
Client sends JWT
   ↓
Backend validates token
   ↓
Access granted
```

---

## Database

Describe the database.

Example:

Tables

- Users
- Orders
- Products
- Payments

Explain relationships.

```
Users
   │1
   │
   │*
Orders
   │
   │*
OrderItems
```

Mention migrations (e.g., Flyway or Liquibase).

---

## 🚀 Deployment & CI/CD

We use **Trunk-Based Development** and deploy our Spring Boot application to **Google Cloud Run** via GitHub Actions. The pipeline ensures code is thoroughly tested and built once before moving through the environments.

### The Workflow

1.  **Pull Requests (Testing):** Any PR opened against `main` automatically runs the test suite (`./gradlew test`). The code cannot be merged until all tests pass.
2.  **Merge to `main` (Build & Push):** * The Java code (Java 21) is packaged using Gradle.
    * A minimal Docker image (based on Eclipse Temurin Alpine) is built and tagged with the specific GitHub commit SHA.
    * The image is pushed to the shared GCP Artifact Registry.
3.  **Staging (Auto-Deploy):** The pipeline automatically updates the Staging Cloud Run service with the newly built Docker image.
4.  **Production (Manual Approval):** The pipeline halts. To deploy to Production, an authorized team member must go to the GitHub Actions tab and approve the release. The *exact same* Docker image is then deployed to Production, ensuring zero environment drift.

### Linting and Formatting
We use **Spotless** for linting and formatting with the google-java-format (Android Open Source Project) ruleset. The CI pipeline runs Spotless on all code and fails the build if any formatting issues are found. This ensures that all code is consistently formatted before being merged.

### Local Development
The application automatically installs a pre-push Git hook to run Spotless on staged files. This ensures that all code is formatted consistently before being pushed.
When building locally, note that the CI pipeline utilizes Gradle's build cache to optimize performance. Ensure your local `./gradlew` file has the correct execution permissions (`chmod +x gradlew`).

## Business Logic

Describe the service layer.

Example:

OrderService

Responsibilities:

- Validate order
- Calculate totals
- Reserve inventory
- Persist order
- Publish events

---

## Error Handling

Explain:

- Exception handling
- Error codes
- Validation responses

Example:

```json
{
  "status": 400,
  "message": "Invalid email"
}
```

---

## Logging

Explain:

- Logging framework
- Log levels
- Correlation IDs
- Request logging

---

## Security

Document:

- Authentication
- Authorization
- Password hashing
- Input validation
- CORS
- CSRF
- Rate limiting

---

## Testing

Describe:

- Unit tests
- Integration tests
- Test containers
- Mocking strategy

Run tests:

```bash
mvn test
```

---

## Deployment

Explain:

- Docker
- Kubernetes
- CI/CD
- Environment configuration

Example:

```bash
docker compose up
```

---

## Monitoring

Explain:

- Health endpoint
- Metrics
- Logging
- Tracing

Example:

```
GET /actuator/health
GET /actuator/metrics
```

---

## Troubleshooting

Common issues.

Example:

Database connection failed

- Check PostgreSQL is running
- Verify DB_URL
- Run migrations

---

## Contributing

Describe development workflow.

Example:

1. Create feature branch
2. Add tests
3. Run formatter
4. Open pull request
