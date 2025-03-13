FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/payment-provider-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]