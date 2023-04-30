FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/accountmanagementsystem-0.0.1-SNAPSHOT.jar app.jar
#EXPOSE 4600
ENTRYPOINT ["java", "-jar", "/app.jar"]
