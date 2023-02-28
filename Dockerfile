FROM openjdk:11.0.11-jre-slim
ARG JAR_FILE=./*-SNAPSHOT.jar
COPY ${JAR_FILE} waldreg-api.jar
RUN ["mkdir", "/file"]
ENTRYPOINT ["java", "-jar", "/waldreg-api.jar"]
