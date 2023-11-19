FROM maven:3.8.4-openjdk-8-slim AS build

WORKDIR /app

COPY ../../pom.xml .
COPY ../../src ./src

RUN mvn clean package -DskipTests

FROM openjdk:8-jdk-slim

RUN mkdir -p /tmp/certs && chmod -R 777 /tmp/certs

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9043
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prd","/app.jar"]