FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

COPY target/reservation-service-1.0.0.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
