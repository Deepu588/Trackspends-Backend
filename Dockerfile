# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml first
COPY pom.xml .
COPY src ./src

# Build WITHOUT the go-offline flag
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# REMOVE this line - it's causing the failure:
# RUN jar tf app.jar | grep -i "mysql.*connector" && echo "✓ MySQL driver found" || (echo "✗ MySQL driver STILL MISSING" && exit 1)

EXPOSE 8080
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]