FROM openjdk:21-jdk-slim

WORKDIR /t_bank_2024

COPY build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]