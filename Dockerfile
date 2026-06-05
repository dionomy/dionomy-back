FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S dionomy && adduser -S dionomy -G dionomy

COPY --from=build /workspace/build/libs/*.jar /app/app.jar

USER dionomy
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
