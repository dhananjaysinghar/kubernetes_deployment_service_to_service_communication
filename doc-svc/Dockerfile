FROM openjdk:11-jdk
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} doc-svc.jar
ENTRYPOINT ["java","-jar","/doc-svc.jar"]