#!/bin/bash

rm -rf insultcounter2/src/main/java/com/redhat/servlets/jsp
rm -rf insultcounter2/target/*
clear
cd insultcounter2
./mvnw compile quarkus:dev
