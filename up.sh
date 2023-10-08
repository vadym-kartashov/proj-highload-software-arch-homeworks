#!/bin/bash

#Build java app
./backend/gradlew build bootJar -p backend
STATUS=$?
if [ $STATUS != 0 ]; then
  echo "Maven build failed."
  exit 1
fi

docker-compose down -v
# Deploy services using docker-compose
docker-compose up --build --remove-orphans -d
exit 0