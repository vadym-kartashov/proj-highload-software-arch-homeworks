FROM openjdk:17-slim

WORKDIR /app
COPY target/*.jar /app/my-app.jar
COPY target/libs /app/libs

CMD ["java", "-jar", "/app/my-app.jar"]
