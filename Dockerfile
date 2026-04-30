# Build stage - Force fresh dependency download
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Clear Maven cache first (critical fix)
RUN rm -rf /root/.m2/repository

# Copy pom.xml
COPY pom.xml .

# Force download ALL dependencies fresh
RUN mvn dependency:go-offline -B -U --fail-never

# Copy source and build
COPY src ./src

# Clean and package - forcing everything fresh
RUN mvn clean package -DskipTests -U

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Verify MySQL driver is present (will show in logs)
RUN jar tf app.jar | grep -i "mysql.*connector" && echo "✓ MySQL driver found" || echo "✗ MySQL driver MISSING"

EXPOSE 8080
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]