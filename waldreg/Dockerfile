FROM openjdk:11.0.11-jre-slim
ARG JAR_FILE=./application-layer/acceptance/build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} waldreg-api.jar
ENTRYPOINT ["java", "-jar", "/waldreg-api.jar"]
