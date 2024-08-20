FROM amazoncorretto:22-alpine

WORKDIR /app

COPY target/nudge-*.jar app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app/app.jar"]