FROM openjdk-17

COPY target/solo-resume-backend-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
