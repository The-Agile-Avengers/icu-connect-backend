FROM openjdk:17

WORKDIR /app

COPY /target/icu-connect-backend-0.0.1-SNAPSHOT.jar /app/

EXPOSE 8080

CMD ["java", "-jar", "icu-connect-backend-0.0.1-SNAPSHOT.jar"]