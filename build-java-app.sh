#!/bin/bash

#Build java app
./backend/gradlew build bootJar -p backend
STATUS=$?
if [ $STATUS != 0 ]; then
  echo "Maven build failed."
  exit 1
fi