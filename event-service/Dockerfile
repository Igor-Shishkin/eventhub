FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app

# 1. Copy only Gradle wrapper and dependency configuration files
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle/ ./gradle/
RUN chmod +x gradlew

# 2. Download dependencies (cached in Docker layer until build.gradle.kts changes)
RUN ./gradlew dependencies --no-daemon

# 3. Copy application source code
COPY src/ ./src/

# 4. Build application JAR using pre-cached dependencies
RUN ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
