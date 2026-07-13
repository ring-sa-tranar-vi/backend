# Ring så Tränar Vi - Backend

## Overview

This backend powers the Ring så Tränar vi fitness app for older adults. It provides REST APIs for managing users, workouts, trainers, activity logs, and feedback, while handling authentication, data storage, and AI-generated workout suggestions.

## Responsibilities

- Provide REST APIs for users, workouts, trainers, activity logs, feedback, and admin operations
- Validate Clerk-issued JWTs and apply role-based access control
- Store and manage application data using JPA repositories
- Generate workout recommendations using Gemini AI
- storage
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

## Project Structure

Follows an MVC-based structure:

- config/ – security and app configuration
- controller/ – API endpoints
- dto/ – data transfer objects
- entity/ – database models
- repository/ – data access layer
- service/ – business logic

## API

- Base URL: http://localhost:8080

- Swagger UI:
http://localhost:8080/swagger-ui/index.html

- Main route groups: 
   /api/users, /api/workouts, /api/trainers, /api/activity-logs, /api/feedbacks, /api/admin, /api/live-token, /api

## Environment Variables

- CLERK_JWT_ISSUER_URI
- SUPABASE_URL
- SUPABASE_API_KEY
- SUPABASE_BUCKET_NAME
- GEMINI_API_KEY

## Getting Started

./gradlew build

./gradlew bootRun

- Runs locally on http://localhost:8080
- Uses H2 database by default but later will use postgres
- Set environment variables to enable authentication, storage, and AI features


## Deployment

Production backend:
https://prod-backend-service-49973934534.europe-west3.run.app/

- Uses PostgreSQL (Neon) in production
- CI/CD and infrastructure setup are documented in the infrastructure repository

## Related Repositories

- Frontend: <[repository link](https://github.com/ring-sa-tranar-vi/frontend)>
- Infrastructure: <[repository link](https://github.com/ring-sa-tranar-vi/infrastructure)>