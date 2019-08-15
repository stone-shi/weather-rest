#!/usr/bin/env bash


if [[ -z "${DEBUG_PORT}" ]]; then
  echo Run in normal model
  java -Djava.security.egd=file:/dev/./urandom -jar $JAVA_OPTS /app.jar
else
  echo "Run in debug mode on port ${DEBUG_PORT}"
  java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=*:${DEBUG_PORT},suspend=n $JAVA_OPTS -jar /app.jar
fi
