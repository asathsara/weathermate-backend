# Use Eclipse Temurin 21 JDK base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven/Gradle build files first (for caching)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copy source code
COPY src ./src

# Build the app (skip tests in Docker build)
RUN ./mvnw clean package -DskipTests

# Expose port Cloud Run uses
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "target/weathermate-backend-0.0.1-SNAPSHOT.jar"]
