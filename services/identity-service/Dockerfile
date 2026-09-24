# ==========================================
# Stage 1: Build
# ==========================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy Maven configuration first
COPY pom.xml .

# Download dependencies
# RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests


# ==========================================
# Stage 2: Runtime
# ==========================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create non-root user
RUN useradd --system --create-home --uid 1001 spring

# Copy application JAR
COPY --from=builder /build/target/*.jar app.jar

# Use non-root user
USER 1001

# Application port
EXPOSE 8081

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]