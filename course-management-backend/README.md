# Backend (Spring Boot) – course-management-backend

Spring Boot service for TechWay. Provides authentication, courses/modules/lessons, enrollments, quizzes, reviews, certificates, payments, and admin/user analytics.

## Stack
- Java 17+ / Spring Boot
- Spring Web, Spring Data JPA, Spring Validation
- (Likely) Spring Security + JWT (see `SecurityConfig`)
- PostgreSQL
- Maven

## Project Structure
```
src/main/java/com/courseapp/coursesystem
├─ config/                # SecurityConfig, CORS, etc.
├─ controller/            # REST endpoints (Admin, Course, Enrollment, Quiz, Review, Certificate, ...)
├─ dto/                   # Request/Response models
├─ entity/                # JPA entities (Course, Module, Lesson, Enrollment, Review, Certificate, ...)
├─ repository/            # Spring Data repositories
├─ service/               # Business logic
└─ CourseManagementBackendApplication.java
```

## Environment
Create `src/main/resources/application.properties` or use `application.yaml`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/techway_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Security
app.security.jwtSecret=CHANGE_ME
app.security.jwtExpiration=3600000
server.port=8080
```

Optional `.env` for Docker Compose:
```env
POSTGRES_DB=techway_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
APP_PORT=8080
```

## Run Locally
```bash
# From course-management-backend/
mvn clean package
mvn spring-boot:run
# Service on http://localhost:8080
```

## Common Endpoints
API is grouped roughly as follows (full list in `docs/api_endpoints.csv`):
- `/api/auth/*` – login/refresh/profile
- `/api/courses/*` – list/get/search courses, outlines
- `/api/enrollments/*` – enroll/unenroll/list
- `/api/progress/*` – lesson/module progress
- `/api/quiz/*` – get/start/submit attempts, history, best-attempt
- `/api/reviews/*` – create/list reviews
- `/api/certificates/*` – generate/download
- `/api/admin/*` – dashboard, analytics, distributions

## Swagger
If you add Springdoc:
```xml
<!-- pom.xml -->
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.6.0</version>
</dependency>
```
Then visit: `http://localhost:8080/swagger-ui.html`

## Docker (Dev) – Compose
Create at repo root:
```yaml
# docker-compose.yml
version: "3.9"
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: ${POSTGRES_DB:-techway_db}
      POSTGRES_USER: ${POSTGRES_USER:-postgres}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-postgres}
    ports: ["5432:5432"]
    volumes: [dbdata:/var/lib/postgresql/data]

  backend:
    build: ./course-management-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${POSTGRES_DB:-techway_db}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER:-postgres}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-postgres}
      SERVER_PORT: 8080
    depends_on: [db]
    ports: ["8080:8080"]

  frontend:
    build: ./techway
    ports: ["5173:5173"]
    environment:
      VITE_API_BASE: http://localhost:8080
    depends_on: [backend]

volumes:
  dbdata: {}
```

**Backend Dockerfile** (`course-management-backend/Dockerfile`):
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

## Nginx (Prod) – Reverse Proxy
Map `/api` → backend and serve frontend static build at `/`.
- Frontend build: `npm run build` → `dist/`
- Nginx `location /api` → `http://backend:8080`

## Troubleshooting
- **CORS**: make sure `SecurityConfig` allows your frontend origin.
- **JWT 401**: verify `Authorization: Bearer <token>` and `app.security.*` secrets.
- **DB errors**: check `SPRING_DATASOURCE_*` env & network to `db` service.
- **Quizzes not loading**: verify endpoints used in UI exist (`/api/quiz/course/{courseId}`, `.../best-attempt`, `.../attempts`).

## What to Commit
- `course-management-backend/README.md` (this file)
- `docs/api_endpoints.csv` (generated inventory)
- Docker files at repo root (`docker-compose.yml`, `nginx.conf` if used)
