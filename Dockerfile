FROM gradle:jdk21 AS build

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts

COPY src src

RUN ./gradlew clean build -x test

FROM openjdk:21-jdk-slim

COPY --from=build /build/libs/*.jar app.jar

EXPOSE 8080

COPY docker-entrypoint.sh /

RUN chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]