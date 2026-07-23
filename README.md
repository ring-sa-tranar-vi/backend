# Ring så Tränar Vi - Backend

## Overview

This backend powers the Ring så Tränar vi fitness app for older adults. It provides REST APIs for managing users, workouts, trainers, activity logs, and feedback, while handling authentication, data storage, and AI-generated workout suggestions.

## Architecture

The backend follows a layered MVC architecture:

```text
Client (Frontend)
        |
        ▼
Controllers (REST API)
        |
        ▼
Services (Business Logic)
        |
        ▼
Repositories (Data Access)
        |
        ▼
Database (H2 / PostgreSQL)
```

- Controllers handle incoming API requests
- Services contain application logic
- Repositories manage database operations
- Entities represent database models
- DTOs handle API request and response objects

## Responsibilities

- Provide REST APIs for users, workouts, trainers, activity logs, feedback, and admin operations
- Validate Clerk-issued JWTs and apply role-based access control
- Store and manage application data using JPA repositories
- Generate workout recommendations using Gemini AI
- Provide API documentation via OpenAPI/Swagger

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server with JWT
- Spring WebSocket
- OpenAPI/Swagger UI via springdoc-openapi
- Postgres Database for local development
- PostgreSQL runtime support
- Clerk for authentication token issuance and validation
- Supabase for file storage
- Google Gemini for AI token and workout recommendation flows


## Authentication

- All protected endpoints require a valid Clerk JWT
- Include token in header:
  Authorization: Bearer <token>

- Roles supported:
  - USER
  - ADMIN
 
- CORS is configured to allow requests from the frontend application

## API

- Base URL: http://localhost:8080

- Swagger UI:
http://localhost:8080/swagger-ui/index.html

- Main route groups: 
   /api/users, /api/workouts, /api/trainers, /api/activity-logs, /api/feedbacks, /api/admin, /api/live-token, /api

## Example Request

GET /api/workouts

Headers:
Authorization: Bearer <token>

## Database
The application uses JPA/Hibernate for database management.

Local development:
- Postgres database

Production:
- PostgreSQL database hosted on Neon

Main entities:

- User
- Trainer
- Workout
- Activity Log
- Feedback

Relationships and database schema are managed through JPA entity mappings.

## Environment Variables

- CLERK_JWT_ISSUER_URI
- GEMINI_API_KEY


## Getting Started
### Prerequisites
- Java 21+
- Gradle Wrapper

### Installation


#### 1. Clone the repository:
```bash
git clone https://github.com/ring-sa-tranar-vi/backend.git
```
#### 2. Set environment variables (see above)

#### 3. Copy files
Copy the folder **ringsatranarvi_files** and it's content from https://drive.google.com/drive/folders/1AAGsyKJFmYUuf5IheBsp0cPiSEkf44nA to the root of the project.

#### 4. Docker Compose for Postgres
Run ```docker compose up``` to start the Postgres database for local development. Postgres automatically populates the database with initial data on first run.
To stop the database, run ```docker compose down```. The data will persist in the Postgres volume. If you want to reset the database, run ```docker compose down -v``` to remove the volume and start fresh.

#### 5. Build the app
```bash
./gradlew build
```
#### 6. Run the app locally
```bash
./gradlew bootRun
```
The application will run locally at http://localhost:8080

#### 7. Open Swagger
http://localhost:8080/swagger-ui/index.html


## Testing

The project uses Spring Boot testing tools for validating backend functionality.

Run tests with:

```bash
./gradlew test
```

## Error Handling

The API returns standard HTTP status codes:

- 200 - Successful request
- 400 - Invalid request
- 401 - Authentication required
- 403 - Insufficient permissions
- 404 - Resource not found
- 500 - Server error

Errors follow a consistent response format.

## Deployment & CI/CD

We use **Trunk-Based Development** and deploy the Spring Boot application to **Google Cloud Run** via GitHub Actions. The pipeline ensures code is thoroughly tested and built once before moving through the environments.

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
When building locally, note that the CI pipeline utilizes Gradle's build cache to optimize performance. Ensure your local `./gradlew` file has the correct execution permissions (`chmod +x gradlew`) if gradle commands in the terminal doesn't work.

## Troubleshooting

### Application fails to start

Check that:
- Required environment variables are configured
- Java 21 is installed
- Gradle wrapper has execution permissions

### Authentication fails

Check:
- Clerk issuer URI is correct
- JWT token is valid
- Authorization header uses:

Authorization: Bearer <token>

## Related Repositories

- Frontend: [Repository Link](https://github.com/ring-sa-tranar-vi/frontend)
- Infrastructure: [Repository Link](https://github.com/ring-sa-tranar-vi/infrastructure)
