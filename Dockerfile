FROM openjdk:11-jdk-slim
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
COPY entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]

