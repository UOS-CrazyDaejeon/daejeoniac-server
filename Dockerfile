FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/uos-crazy-daejeon-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]