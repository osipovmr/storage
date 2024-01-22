FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/storage-1.0-SNAPSHOT.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","/app.jar"]