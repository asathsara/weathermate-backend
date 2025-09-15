# WeatherMate Backend

WeatherMate is a Spring Boot backend application that provides weather data and user authentication using JWT. It integrates with the OpenWeatherMap API and supports user registration, login, and search history tracking.

## Features

* User registration and login with JWT authentication
* Secure access to weather data
* Search history tracking per user
* Refresh token support via HTTP-only cookies
* Passwords hashed with BCrypt
* Integration with OpenWeatherMap API
* Automated CI/CD deployment to Google Cloud Run

## Technologies Used

* Java 17+
* Spring Boot
* Spring Security (JWT)
* JPA/Hibernate
* Lombok
* OpenWeatherMap API
* Maven
* Docker
* GitHub Actions (CI/CD)
* Google Cloud Run

## Getting Started

### Prerequisites

* Java 17 or higher
* Maven
* Docker (for local builds or testing)
* Google Cloud account (for Cloud Run deployment)

### Setup

1. **Clone the repository:**

   ```bash
   git clone <your-repo-url>
   cd weathermate-backend
   ```
2. **Configure environment variables:**

   * Edit `src/main/resources/env.properties` and set:

     ```properties
     jwt.secret=<your-jwt-secret-base64>
     openweathermap.api.key=<your-openweathermap-api-key>
     ```
3. **Build and run locally:**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## API Endpoints

### Authentication

* `POST /register` — Register a new user

  * Request body: `{ "username": "user", "password": "pass" }`
* `POST /login` — Login and receive JWT access/refresh tokens

  * Request body: `{ "username": "user", "password": "pass" }`
  * Response: `{ "accessToken": "...", "message": "Login successful" }`
  * Sets HTTP-only cookie: `refreshToken`
* `GET /refresh` — Get a new access token using the refresh token cookie

  * Response: `{ "accessToken": "..." }`

### Weather

* `GET /api/weather/{city}` — Get weather data for a city (requires authentication)

  * Response: WeatherDto object
* `GET /api/history` — Get user's search history (requires authentication)

  * Response: List of SearchHistoryDto

## Authentication & Security

* JWT access tokens are required for protected endpoints.
* Refresh tokens are stored in HTTP-only cookies for security.
* Passwords are hashed using BCrypt.
* Stateless session management.

### How JWT Authentication Works

* **Access Token:** Short-lived, sent in the `Authorization` header.
* **Refresh Token:** Long-lived, stored as an HTTP-only cookie, used at `/refresh` to obtain new access tokens.
* If both tokens expire, the user must log in again.

## Data Models

* **User:** id, username, password
* **SearchHistory:** id, city, searchedAt, temperature, user

## Example Usage

### Register

```bash
curl -X POST http://localhost:8080/register -H "Content-Type: application/json" -d '{"username":"test","password":"testpass"}'
```

### Login

```bash
curl -X POST http://localhost:8080/login -H "Content-Type: application/json" -d '{"username":"test","password":"testpass"}'
```

### Get Weather (Authenticated)

```bash
curl -X GET http://localhost:8080/api/weather/London -H "Authorization: Bearer <accessToken>"
```

### Get Search History (Authenticated)

```bash
curl -X GET http://localhost:8080/api/history -H "Authorization: Bearer <accessToken>"
```

## Configuration

* `application.properties`:

  * `spring.application.name=weathermate-backend`
  * `spring.config.import=classpath:env.properties`
  * `spring.jpa.hibernate.ddl-auto=update`
  * `jwt.access.expiration=60000` (1 min)
  * `jwt.refresh.expiration=604800000` (7 days)
* `env.properties`:

  * `jwt.secret` — Base64-encoded secret for JWT
  * `openweathermap.api.key` — Your OpenWeatherMap API key

## CI/CD Deployment (GitHub Actions + Cloud Run)

WeatherMate is automatically built, tested, containerized, and deployed to Google Cloud Run using GitHub Actions.

### Workflow Summary

* Triggered on push or pull request to `main` branch.
* Steps:

  1. Checkout code
  2. Setup JDK 21
  3. Authenticate to GCP using service account
  4. Build Spring Boot JAR (skipping tests if needed)
  5. Run tests
  6. Build Docker image
  7. Push Docker image to Google Artifact Registry
  8. Deploy to Cloud Run with environment variables

### Secrets Needed

* `DB_URL`, `DB_USER`, `DB_PASSWORD`
* `OWM_URL`, `OWM_KEY`
* `JWT_SECRET`
* `CORS_ALLOWED_ORIGINS`
* `GCP_SA_KEY`

### Notes

* Cloud Run deploys the latest container automatically.
* Environment variables are injected during deployment, keeping secrets secure.
* Tests are executed before deployment to prevent catastrophes (aka broken builds) 😅

## License

MIT
