FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine
LABEL authors="vkartashov"
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]