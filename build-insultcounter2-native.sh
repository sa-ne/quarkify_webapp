#!/bin/bash

NATIVE_BUILD=true
CREATE_CONTAINER=true
clear
cd insultcounter2
./mvnw package -Pnative -Dquarkus.native.container-build=${NATIVE_BUILD}

