FROM maven:3.8.4-openjdk-21 AS build

WORKDIR /app

COPY pom.xml ./
COPY mvnw ./
COPY .mvn ./.mvn

COPY src ./src

RUN chmod +x ./mvnw

RUN ./mvnw clean package -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/todoapp2-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]