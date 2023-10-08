#!/bin/bash
docker-compose down -v
# Deploy services using docker-compose
docker-compose up --build --remove-orphans -d
exit 0