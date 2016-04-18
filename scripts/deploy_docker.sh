#!/usr/bin/env bash
docker login -e="$DOCKER_EMAIL" -u="$DOCKER_USERNAME" -p="$DOCKER_PASSWORD"
sbt tck-runner/docker:publish akka-http-mock-server/docker:publish