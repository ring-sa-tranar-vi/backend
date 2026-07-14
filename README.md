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
- H2 Database for local development
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
- H2 in-memory database

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
- SUPABASE_URL
- SUPABASE_API_KEY
- SUPABASE_BUCKET_NAME
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
#### 2. Set environment variables (see below)

#### 3. Build the app
```bash
./gradlew build
```
#### 4. Run the app locally
```bash
./gradlew bootRun
```
The application will run locally at http://localhost:8080

#### 5. Open Swagger
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

## Deployment

Production backend:
https://prod-backend-service-49973934534.europe-west3.run.app/

- Uses PostgreSQL (Neon) in production
- CI/CD and infrastructure setup are documented in the infrastructure repository

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

- Frontend: [repository link](https://github.com/ring-sa-tranar-vi/frontend)
- Infrastructure: <[repository link](https://github.com/ring-sa-tranar-vi/infrastructure)>
