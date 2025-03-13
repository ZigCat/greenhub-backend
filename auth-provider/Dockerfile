FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

RUN apt-get update
RUN apt update && apt install -y curl

COPY --from=builder /app/target/auth-provider-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]