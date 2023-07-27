#!/bin/bash

# Build the Spring Boot app
cd java-sample-app
./mvnw clean package -DskipTests=true
STATUS=$?
if [ $STATUS != 0 ]; then
  echo "Maven build failed."
  exit 1
fi
cd ..

docker-compose down -v
# Deploy services using docker-compose
docker-compose up -d --build --remove-orphans
exit 0