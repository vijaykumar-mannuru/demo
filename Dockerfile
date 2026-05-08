FROM eclipse-temurin:17

LABEL maintainer="vijaykumar"

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "springboot-demo.jar"]