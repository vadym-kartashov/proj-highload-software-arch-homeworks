#!/bin/bash

# Build java app
#cd redis-java-app
#  #mvn -B dependency:go-offline
#  mvn clean install -DskipTests=true
#STATUS=$?
#if [ $STATUS != 0 ]; then
#  echo "Maven build failed."
#  exit 1
#fi
#cd ..

docker-compose down -v
# Deploy services using docker-compose
docker-compose up --build --remove-orphans
exit 0