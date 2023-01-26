#!/bin/sh
./mvnw clean package -DskipTests
cp target/onlineshop-0.0.1-SNAPSHOT.jar src/main/docker
docker-compose build
docker-compose up