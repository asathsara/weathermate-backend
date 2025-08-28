# WeatherMate Backend

WeatherMate is a Spring Boot backend application that provides weather data and user authentication using JWT. It integrates with the OpenWeatherMap API and supports user registration, login, and search history tracking.

## Features
- User registration and login with JWT authentication
- Secure access to weather data
- Search history tracking per user
- Refresh token support via HTTP-only cookies
- Passwords hashed with BCrypt
- Integration with OpenWeatherMap API

## Technologies Used
- Java 17+
- Spring Boot
- Spring Security (JWT)
- JPA/Hibernate
- Lombok
- OpenWeatherMap API
- Maven

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven

### Setup
1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd weathermate-backend
   ```
2. **Configure environment variables:**
   - Edit `src/main/resources/env.properties` and set:
     ```properties
     jwt.secret=<your-jwt-secret-base64>
     openweathermap.api.key=<your-openweathermap-api-key>
     ```
3. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## API Endpoints

### Authentication
- `POST /register` — Register a new user
  - Request body: `{ "username": "user", "password": "pass" }`
- `POST /login` — Login and receive JWT access/refresh tokens
  - Request body: `{ "username": "user", "password": "pass" }`
  - Response: `{ "accessToken": "...", "message": "Login successful" }`
  - Sets HTTP-only cookie: `refreshToken`
- `GET /refresh` — Get a new access token using the refresh token cookie
  - Response: `{ "accessToken": "..." }`

### Weather
- `GET /api/weather/{city}` — Get weather data for a city (requires authentication)
  - Response: WeatherDto object
- `GET /api/history` — Get user's search history (requires authentication)
  - Response: List of SearchHistoryDto

## Authentication & Security
- JWT access tokens are required for protected endpoints.
- Refresh tokens are stored in HTTP-only cookies for security.
- Passwords are hashed using BCrypt.
- Stateless session management.

### How JWT Authentication Works
WeatherMate uses JWT (JSON Web Token) for stateless authentication:

- **Access Token:**
  - Short-lived (default: 1 minute).
  - Sent in the `Authorization: Bearer <token>` header for each request to protected endpoints.
  - Contains the username and expiration time.
  - If expired, the user must use the refresh token to obtain a new access token.

- **Refresh Token:**
  - Long-lived (default: 7 days).
  - Sent as an HTTP-only cookie (`refreshToken`) after successful login.
  - Not accessible via JavaScript, reducing XSS risk.
  - Used at `/refresh` endpoint to obtain a new access token when the old one expires.
  - If the refresh token is invalid or expired, the user must log in again.

**Flow:**
1. User logs in and receives both access and refresh tokens.
2. Access token is used for API requests; refresh token is stored securely in a cookie.
3. When the access token expires, the client calls `/refresh` (with the cookie) to get a new access token.
4. If both tokens expire, the user must log in again.

**Security Notes:**
- Access tokens are short-lived to minimize risk if leaked.
- Refresh tokens are stored as HTTP-only cookies to prevent client-side access.
- Passwords are always hashed before storage.

## Data Models
- **User**: id, username, password
- **SearchHistory**: id, city, searchedAt, temperature, user

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
- `application.properties`:
  - `spring.application.name=weathermate-backend`
  - `spring.config.import=classpath:env.properties`
  - `spring.jpa.hibernate.ddl-auto=update`
  - `jwt.access.expiration=60000` (1 min)
  - `jwt.refresh.expiration=604800000` (7 days)
- `env.properties`:
  - `jwt.secret` — Base64-encoded secret for JWT
  - `openweathermap.api.key` — Your OpenWeatherMap API key

## License
MIT
