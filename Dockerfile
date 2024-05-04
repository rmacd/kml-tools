#FROM eclipse-temurin:17-jdk-alpine
#RUN mkdir /opt/app
#COPY target/kml-generator-0.0.1-SNAPSHOT.jar /opt/app/kml-generator.jar
#CMD ["java", "-jar", "/opt/app/kml-generator.jar"]

FROM openjdk:17-jdk-alpine as builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:17-jdk-alpine
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]