# =========================
# Build stage
# =========================
FROM gradle:8.7-jdk17 AS build

WORKDIR /app

# Copy only necessary files first (for caching)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Download dependencies (better caching)
RUN ./gradlew dependencies --no-daemon || true

# Copy full project
COPY . .

# Build jar
RUN ./gradlew clean build -x test --no-daemon
# =========================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy built jar from previous stage
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]