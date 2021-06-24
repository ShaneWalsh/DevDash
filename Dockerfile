FROM adoptopenjdk/openjdk11
ARG JAR_FILE=web/target/*.jar
COPY ${JAR_FILE} ddapp.jar
ENTRYPOINT ["java","-jar","/ddapp.jar"]
