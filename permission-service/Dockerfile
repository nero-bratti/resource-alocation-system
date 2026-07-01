FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . .
RUN mvn -pl permission-service -am -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/permission-service/target/permission-service-0.0.1-SNAPSHOT.jar ./app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
