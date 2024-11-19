# Используем официальный образ JDK 21 для сборки приложения
FROM eclipse-temurin:21-jdk-alpine AS build

# Устанавливаем рабочую директорию
WORKDIR /app
# Копируем gradle wrapper и build.gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
# Копируем весь исходный код
COPY src src
# Загружаем зависимости и собираем проект
RUN ./gradlew build
# Очищаем образ от ненужных данных и копируем собранный jar файл
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/your-app.jar app.jar
# Указываем команду, которая будет выполняться при запуске контейнера
ENTRYPOINT ["java", "-jar", "app.jar"]