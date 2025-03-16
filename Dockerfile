# Используем официальный образ OpenJDK
FROM openjdk:23-jdk-slim

# Указываем рабочую директорию
WORKDIR /app

# Копируем JAR-файл (созданный Gradle-сборкой) в контейнер
COPY build/libs/*.jar app.jar

# Открываем порт, на котором будет работать приложение
EXPOSE 8083

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]