#!/bin/bash

NATIVE_BUILD=true
CREATE_CONTAINER=true
clear
cd insultcounter2
./mvnw package -Pnative -Dquarkus.native.container-build=${NATIVE_BUILD}
podman build -f src/main/docker/Dockerfile.native -t quarkus/native/insult .

