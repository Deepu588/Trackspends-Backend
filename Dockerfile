# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml first
COPY pom.xml .
COPY src ./src

# Build WITHOUT the go-offline flag (this is likely the problem)
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Verify MySQL driver is in the JAR
RUN jar tf app.jar | grep -i "mysql.*connector" && echo "✓ MySQL driver found" || (echo "✗ MySQL driver STILL MISSING" && exit 1)

EXPOSE 8080
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]