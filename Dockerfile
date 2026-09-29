FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

COPY src src

RUN ./mvnw clean package \
    -Dmaven.test.skip=true \
    -Dmaven.wagon.http.retryHandler.count=5 \
    -ntp

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S modelrisk \
    && adduser -S modelrisk -G modelrisk

COPY --from=build /workspace/target/*.jar app.jar

USER modelrisk

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]