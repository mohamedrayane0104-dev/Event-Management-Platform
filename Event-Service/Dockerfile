FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

COPY target/event-service-1.0.0.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]