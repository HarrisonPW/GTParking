FROM openjdk:8
VOLUME /tmp
EXPOSE 8082
ARG JAR_FILE=target/GTParking-Service-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-r","/app.jar"]