FROM openjdk:21-jdk-slim AS build

COPY target/reviews-0.0.1-SNAPSHOT.jar reviews-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/reviews-0.0.1-SNAPSHOT.jar"]
