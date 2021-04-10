#!/bin/bash

rm -rf jspServlet/src/main/java/com/redhat/servlets/jsp
rm -rf jspServlet/target/*
clear
cd jspServlet
./mvnw compile quarkus:dev
