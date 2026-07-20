# Admin Endpoints, Tests, and Tools

This document summarizes all admin API endpoints in the backend, current test coverage, and practical tools/commands for testing.

## 1) Base Setup

- Base URL (local): `http://localhost:8080`
- Admin controller base path: `/api/admin`
- Authentication: Bearer JWT (required)
- Authorization rule: each admin endpoint checks `service.isAdmin(clerkId)` and returns `403` when user is not admin.

Common headers for Postman:

- `Authorization: Bearer <YOUR_JWT>`
- `Content-Type: application/json`

## 2) Full Admin Endpoint List

## `GET /api/admin`
- Purpose: Admin greeting/check endpoint.
- Auth: Admin only.
- Response: `200 OK` text message, or `403`.

## `GET /api/admin/workouts/feedback-summary`
- Purpose: Workout feedback analytics summary.
- Auth: Admin only.
- Response: `List<AdminWorkoutFeedbackSummaryDTO>`.

## `GET /api/admin/feedbacks`
- Purpose: Recent feedback entries.
- Auth: Admin only.
- Response: `List<AdminRecentFeedbackDTO>`.

## `GET /api/admin/users/count`
- Purpose: Total and active user counts.
- Auth: Admin only.
- Response: `AdminUserCountDTO`.

## `GET /api/admin/users`
- Purpose: Admin user list/summary.
- Auth: Admin only.
- Response: `List<AdminUserSummaryDTO>`.

## `PUT /api/admin/users/{id}`
- Purpose: Admin update of a user.
- Auth: Admin only.
- Body (JSON):

```json
{
  "name": "Updated Name",
  "intensityLevel": 3,
  "context": "some notes",
  "trainerId": 2,
  "role": "USER"
}
```

- Response: `200 OK` text message, `403`, or service error (for example `404`).

## `DELETE /api/admin/users/{id}`
- Purpose: Admin delete user.
- Auth: Admin only.
- Response: `204 No Content`, `403`, or service error.

## `GET /api/admin/activity-logs/recent`
- Purpose: Recent activity feed.
- Auth: Admin only.
- Response: `List<AdminRecentActivityDTO>`.

## `GET /api/admin/workouts/usage`
- Purpose: Workout usage statistics.
- Auth: Admin only.
- Response: `List<AdminWorkoutUsageDTO>`.

## `GET /api/admin/trainers/overview`
- Purpose: Trainer overview metrics.
- Auth: Admin only.
- Response: `List<AdminTrainerOverviewDTO>`.

## `GET /api/admin/organisations`
- Purpose: List organisations.
- Auth: Admin only.
- Response: `List<AdminOrganisationResponseDTO>`.

## `POST /api/admin/organisations`
- Purpose: Create organisation.
- Auth: Admin only.
- Body (JSON):

```json
{
  "name": "Organisation A",
  "description": "Optional description"
}
```

- Response: `AdminOrganisationResponseDTO`.

## `DELETE /api/admin/organisations/{id}`
- Purpose: Delete organisation.
- Auth: Admin only.
- Behavior: associated events are deleted first in service.
- Response: `204 No Content`.

## `GET /api/admin/events`
- Purpose: List events.
- Auth: Admin only.
- Response: `List<AdminEventResponseDTO>`.

## `POST /api/admin/events`
- Purpose: Create event.
- Auth: Admin only.
- Body (JSON):

```json
{
  "name": "Event A",
  "description": "Optional description",
  "time": "2026-08-01T10:00:00",
  "organisationId": 1
}
```

- Response: `AdminEventResponseDTO`.

## `DELETE /api/admin/events/{id}`
- Purpose: Delete event.
- Auth: Admin only.
- Response: `204 No Content`.

## 3) Where the API Is Implemented

- Controller: `src/main/java/dev/salt/Ring20/controller/AdminController.java`
- Service logic: `src/main/java/dev/salt/Ring20/service/AdminService.java`
- New entities:
  - `src/main/java/dev/salt/Ring20/entity/Organisation.java`
  - `src/main/java/dev/salt/Ring20/entity/Event.java`
- New repositories:
  - `src/main/java/dev/salt/Ring20/repository/OrganisationRepository.java`
  - `src/main/java/dev/salt/Ring20/repository/EventRepository.java`

## 4) Test Coverage (Current)

Primary test file:
- `src/test/java/dev/salt/Ring20/controller/AdminControllerTest.java`

Covered endpoints include:
- `GET /api/admin/users/count` (admin + non-admin)
- `GET /api/admin/users` (admin + non-admin)
- `PUT /api/admin/users/{id}` (admin + non-admin)
- `DELETE /api/admin/users/{id}` (admin)
- `GET /api/admin/activity-logs/recent` (admin + non-admin)
- `GET /api/admin/workouts/usage` (admin + non-admin)
- `GET /api/admin/trainers/overview` (admin + non-admin)
- `GET /api/admin/organisations` (admin + non-admin)
- `POST /api/admin/organisations` (admin + non-admin)
- `DELETE /api/admin/organisations/{id}` (admin + non-admin)
- `GET /api/admin/events` (admin + non-admin)
- `POST /api/admin/events` (admin + non-admin)
- `DELETE /api/admin/events/{id}` (admin + non-admin)

Not explicitly covered in this test class yet:
- `GET /api/admin`
- `GET /api/admin/workouts/feedback-summary`
- `GET /api/admin/feedbacks`
- non-admin case for `DELETE /api/admin/users/{id}`

## 5) Tools and How to Use Them

## Postman
1. Create an environment variable:
   - `baseUrl = http://localhost:8080`
2. Set Authorization tab to Bearer Token and paste your JWT.
3. Call endpoints with `{{baseUrl}}/api/admin/...`.

## Swagger UI
- URL: `http://localhost:8080/swagger-ui.html`
- Useful for quick endpoint discovery and payload checks.

## Gradle Test Commands

```powershell
cd C:\Users\venuj\salt\pgp\trainingapp\backend
.\gradlew.bat test --tests dev.salt.Ring20.controller.AdminControllerTest
.\gradlew.bat test
```

## Optional cURL examples

```bash
curl -X GET "http://localhost:8080/api/admin/events" -H "Authorization: Bearer <JWT>"
curl -X POST "http://localhost:8080/api/admin/organisations" -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" -d '{"name":"Org A","description":"Desc"}'
```

## 6) Quick Troubleshooting

- `403 Forbidden`: token user is not admin.
- `401 Unauthorized`: missing/invalid token.
- `404 Not Found`: wrong ID or missing resource.
- `409 Conflict`: duplicate organisation name on create.
- `400 Bad Request`: invalid payload (for example missing name or missing event `time`).

