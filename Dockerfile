# syntax=docker/dockerfile:1.7
FROM gradle:8.8-jdk21-alpine AS builder
WORKDIR /workspace

COPY . .
RUN gradle --no-daemon clean bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 9080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
