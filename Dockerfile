#FROM eclipse-temurin:21-jre
#COPY target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM maven:3.8.1-openjdk-17 AS builder
COPY . .
RUN mvn clean package -Pproduction

FROM eclipse-temurin:21-jre
COPY --from=builder target/byteme-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]