# Use the official OpenJDK base image
FROM openjdk:17-jdk-slim

# Metadata as described above
LABEL maintainer="your_email@example.com" \
      version="1.0" \
      description="Spring Boot Application"

# Set the current working directory inside the container
WORKDIR /app

# Copy the jar file from the host into the current directory inside the container
COPY build/libs/*.jar app.jar

# Set the startup command
ENTRYPOINT ["java","-jar","app.jar"]
