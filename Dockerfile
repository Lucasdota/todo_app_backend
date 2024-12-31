# Use a multi-stage build to reduce the final image size
# Stage 1: Build the application
FROM maven:3.8.4-openjdk-21 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and the Maven wrapper
COPY pom.xml ./
COPY mvnw ./
COPY .mvn ./.mvn

# Copy the rest of the application code
COPY src ./src

# Make the Maven wrapper executable
RUN chmod +x ./mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:21-jre-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/todoapp2-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port (default Spring Boot port is 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]