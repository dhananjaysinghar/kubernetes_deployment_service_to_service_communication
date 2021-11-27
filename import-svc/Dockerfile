FROM openjdk:11-jdk
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} import-svc.jar
ENTRYPOINT ["java","-jar","/import-svc.jar"]