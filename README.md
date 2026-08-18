# Ring så tränar vi - Backend

## Overview

This backend powers the Ring så tränar vi fitness app. It provides REST APIs for managing users, workouts, trainers, activity logs, and feedback, while handling authentication, data storage, and AI-generated workout suggestions.

## Features / Responsibilities

- Provide REST APIs for users, workouts, trainers, activity logs, feedback, and admin operations
- Validate Clerk-issued JWTs and apply role-based access control
- Store and manage application data using JPA repositories
- AI driven workout selection using Gemini AI
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
- Neon
- Google Gemini for AI token and workout recommendation flows

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

## Project Structure

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── .../
│   │   │       ├── controller/       # REST API endpoints
│   │   │       ├── service/         
│   │   │       │    ├── ai           # Ai logic 
│   │   │       │    ├── data         # Data holders
│   │   │       │    ├── security     # Security logic
│   │   │       │    └── storage      # Storage logic
│   │   │       ├── repository/       # Database access
│   │   │       ├── entity/           # Entities and enums
│   │   │       │    └── enums/
│   │   │       ├── dto/              # Request and response objects
│   │   │       │    ├── user/
│   │   │       │    ├── workout/
│   │   │       │    ├── organization/
│   │   │       │    ├── event/
│   │   │       │    ├── trainer/
│   │   │       │    ├── admin/
│   │   │       │    ├── callback/
│   │   │       │    ├── feedback/
│   │   │       │    ├── activityLog/
│   │   │       │    ├── calendarEvent/
│   │   │       │    ├── company/
│   │   │       │    └── fcmToken/
│   │   │       ├── config/            # Application configuration
│   │   │       └── mapper/            # Mapper classes
│   │   ├──kotlin/                     # Kotlin setup
│   │   │
│   │   └── resources/   
│   │         └── application.yaml    # Application configuration
│   │      
│   └── test/
│       └── java/                      # Automated tests
├── build.gradle.kts                   # Gradle build configuration
├── gradlew                            # Gradle Wrapper
├── gradlew.bat                        # Gradle Wrapper for Windows
└── settings.gradle.kts                # Gradle project settings
```

## Getting Started

### Prerequisites

- Git
- Java 21+
- Docker
- Docker Compose

### Installation

Clone the repository

```bash
git clone https://github.com/ring-sa-tranar-vi/backend.git 
cd ring-sa-tranar-vi/backend
```

### Environment Variables

The application requires environment variables for database access, authentication, and AI functionality.

| Variable | Required | Description |
| --- | --- | --- |
| ` SPRING_DATASOURCE_URL` | `for local devlopment` | `Database connection URL used by the application` |
| ` SPRING_DATASOURCE_USERNAME` | `for local devlopment` | `Username used to connect to the database` |
| ` SPRING_DATASOURCE_PASSWORD` | `for local devlopment` | `Password used to connect to the database` |
| ` PORT` | `yes` | `Port on which the backend application runs` |
| ` CLERK_JWT_ISSUER_URI` | `yes` | `Clerk issuer URI used to validate authentication tokens` |
| ` GEMINI_API_KEY` | `yes` | `API key used to access the Gemini API` |
| ` OPENAI_API_KEY` | `yes` | `API key used to access the OpenAI API` |
| ` GRAFANA_OTLP_AUTH` | `yes` | `Authentication credentials used when sending telemetry to Grafana` |
| ` GRAFANA_OTLP_URL` | `yes` | `Endpoint URL used for sending telemetry to Grafana` |
| ` FIREBASE_CONFIG_JSON` | `yes` | `Firebase service account configuration used by the backend` |

**Grafana** is used for monitoring and observability of the backend and infrastructure. The Grafana configuration is provided through environment variables and is **only required for deployed environments, not for local development**.

### Run Locally

If you want to see the differnet trainers do the next step, otherwise skip it.

#### Copy files
Copy the folder **ringsatranarvi_files** and it's content
from https://drive.google.com/drive/folders/1AAGsyKJFmYUuf5IheBsp0cPiSEkf44nA to the root of the project.

#### Start the database

The project uses PostgreSQL for local development. PostgreSQL is started using ```docker compose up``` and starts a
container on port ```5432```.
The local database is configured as:

| Setting | Value |
| --- | --- |
| ` Database` | `ring20_db` |
| ` Username` | `postgres` |
| ` password` | `password` |
| ` Host` | `localhost` |
| ` post` | `5432` |

The database initialization scripts in db-init/ are automatically executed when the PostgreSQL container is initialized.

To stop the database, run ```docker compose down```. The data will persist in the Postgres volume. If you want to reset
the database, run ```docker compose down -v``` to remove the volume and start fresh.

#### Run the backend

Once the PostgreSQL database is running localy, start the Spring Boor application:

```bash
./gradlew bootRun
```

The application will run locally at http://localhost:8080

### Open Swagger

Once the application is running, the API documentation is available through Swagger UI:
http://localhost:8080/swagger-ui/index.html

## API

The backend exposes a REST API for managing users, workouts, trainers, activity logs, feedback, organizations, events
and AI-generated workout suggestions.

The complete API specification is available through Swagger UI when running the application locally:

`http://localhost:8080/swagger-ui/index.html`

### Authentication

The API uses [Clerk](https://clerk.com/) for authentication. Protected endpoints require a valid Clerk-issued JWT.

Include the JWT in the `Authorization` header using the Bearer scheme:

```
Authorization: Bearer <clerk-jwt>
```

CORS is configured to allow requests from the frontend application

Roles supported:

- USER
- ADMIN

### Endpoints

The main API resources are:

- /api/users
- /api/workouts
- /api/trainers
- /api/activity-logs
- /api/feedbacks
- /api/company
- /api/organizations
- /api/events
- /api/admin
- /api/live-tokens
- /api

### Example Request

Request:

```
GET /api/users/1

Headers:
Authorization: Bearer <token>
```

Response:

```
{
  "id": 1,
  "name": "string",
  "intensityLevel": 0,
  "context": "string",
  "isAdmin": true,
  "trainerId": 0,
  "city": "string",
  "onboarding": true
}
```

### Error Handling

The API returns standard HTTP status codes:

- 200 - Successful request
- 201 - Created
- 204 - No Content
- 400 - Invalid request
- 401 - Authentication required
- 403 - Insufficient permissions
- 404 - Resource not found
- 409 - Request conflict
- 500 - Server error

Errors follow a consistent response format.

## Database

The application uses JPA/Hibernate for database management.

#### Local development:

**ALWAYS** use the provided Docker Compose setup for local development to ensure consistent database configuration and
avoid exceeding NEON's free plan limit.

- Postgres database

Production:

- PostgreSQL database hosted on Neon
- media files are stored on Google Cloud Platform

#### Main entities:

- User
- Trainer
- Workout
- Activity Log
- Feedback
- Organization
- Organization Application
- Event
- User workout preference
- Callback preference

#### Updating entities:

***If an entity is added and/or updated**, create a .sql in the db-init folder to update the database with the changes.
Follow the naming convention such as 01-xxx.sql, 02-xxx.sql etc. and use the next number in chronological order. These
files are used to update the database on NEON and in docker compose.*

If you need to reset the database on local development environment, run ```docker compose down -v``` to remove the
volume and start fresh with ```docker compose up```.

## Development

This section describes the tools and workflows used when developing the backend.

### Testing

The project uses Spring Boot testing tools for validating backend functionality.

Run all tests with:

```bash
./gradlew test
```

### Linting and Formatting

We use **Spotless** for linting and formatting with the google-java-format (Android Open Source Project) ruleset.

To automatically format the code run:

```bash
./gradlew spotlessApply
```

To check whether the code is correctly formatted without making changes run:

```bash
./gradlew spotlessCheck
```

The CI pipeline runs Spotless on all code and fails the build if any formatting issues are found. This ensures that all
code is consistently formatted before being merged.

<!--
### Local Development
The backend runs locally using Spring Boot, while PostgreSQL is provided through Docker Compose.
**ALWAYS** use the provided Docker Compose setup for local development to ensure consistent database configuration and avoid exceeding NEON's free plan limit.
Start the database:
```bash
docker compose up -d
```
Start the backend:
```bash
./gradlew bootRun
```
The application is then available at:
http://localhost:8080
See Getting Started for the complete local setup instructions.
The project uses the Gradle Wrapper, so Gradle does not need to be installed separately.
If the Gradle Wrapper is not executable on Linux or macOS, run:
```bash
chmod +x gradlew
```
-->

### Development Workflow

The project uses Trunk-Based Development. Developers work on short-lived branches and create pull requests against the `main` branch.

Changes should be kept small and focused to make them easier to review and integrate.

Before creating a pull request:

1. Run the test suite:

```bash
./gradlew test
```

2. Run code formatting:

```bash
./gradlew spotlessApply
```

3. Commit and push the changes.
  
4. Create a pull request for review.
  

The project also uses a pre-push Git hook that runs Spotless on staged files to help prevent incorrectly formatted code
from being pushed.

CI runs the relevant checks again before changes can be merged.

## Deployment & CI/CD

The Spring Boot application is deployed to **Google Cloud Run** via GitHub Actions.
The pipeline ensures code is thoroughly tested and built once before moving through the environments.

### Workflow

1. **Pull Requests (Testing):** Any PR opened against `main` automatically runs the test suite (`./gradlew test`). The
  code cannot be merged until all tests pass.
2. **Merge to `main` (Build & Push):** * The Java code (Java 21) is packaged using Gradle.
  - A minimal Docker image (based on Eclipse Temurin Alpine) is built and tagged with the specific GitHub commit SHA.
  - The image is pushed to the shared GCP Artifact Registry.
3. **Staging (Auto-Deploy):** The pipeline automatically updates the Staging Cloud Run service with the newly built
  Docker image.
4. **Production (Manual Approval):** The pipeline halts. To deploy to Production, an authorized team member must go to
  the GitHub Actions tab and approve the release. The *exact same* Docker image is then deployed to Production,
  ensuring zero environment drift.

## Troubleshooting

### Application fails to start

Check that:

- Required environment variables are configured.
- Java 21 is installed.
- Gradle wrapper has execution permissions.
- On Linux or macOS, if the Gradle Wrapper is not executable, run:

```bash
chmod +x gradlew
```

### Authentication fails

Check:

- Clerk issuer URI is correct
- JWT token is valid
- Authorization header uses:

```
Authorization: Bearer <token>
```

## Related Repositories

- Frontend: [Repository Link](https://github.com/ring-sa-tranar-vi/frontend)
- Infrastructure: [Repository Link](https://github.com/ring-sa-tranar-vi/infrastructure)
