FROM maven:3-openjdk-17

COPY . /app

WORKDIR /app

RUN mvn clean package -DskipTests

COPY target/solo-resume-backend-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
