FROM maven:3.9.6-eclipse-temurin-17 AS build
ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY pom.xml $HOME/
RUN --mount=type=cache,target=/root/.m2 mvn -f $HOME/pom.xml dependency:go-offline
COPY src/ $HOME/src/
RUN --mount=type=cache,target=/root/.m2 mvn -f $HOME/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
COPY --from=build /app/target/payment-provider-0.0.1.jar app.jar
ENTRYPOINT java -jar app.jar