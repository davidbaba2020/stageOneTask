FROM openjdk:17-alpine
EXPOSE 8080
COPY ./target/stageOneTask.jar ./stageOneTask.jar
ENTRYPOINT ["java", "-jar", "stageOneTask.jar"]
