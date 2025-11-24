# Fase de construcción
FROM maven:3.6.3-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn package -DskipTests

# Contenedor final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/gft-elearning-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
