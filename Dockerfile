FROM openjdk:11.0.11-jre-slim

ENV DOCKERIZE_VERSION v0.2.0

RUN apt-get update && apt-get install -y wget

ENV DOCKERIZE_VERSION v0.6.1
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

ARG JAR_FILE=./*-SNAPSHOT.jar
COPY ${JAR_FILE} waldreg-api.jar
RUN ["mkdir", "/file"]
ENTRYPOINT ["dockerize", "-wait", "tcp://database:3306", "-timeout", "20s"]
CMD ["java", "-jar", "/waldreg-api.jar"]
