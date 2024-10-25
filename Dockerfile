# Этап сборки
FROM gradle:8.8-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
RUN gradle build --no-daemon --parallel --stacktrace -x test || true
COPY . .
RUN gradle clean build --no-daemon --stacktrace -x test

FROM openjdk:17-jdk-slim
COPY --from=build /app/build/libs/*.jar application.jar
EXPOSE 18080
ENTRYPOINT ["java", "-jar", "application.jar"]
