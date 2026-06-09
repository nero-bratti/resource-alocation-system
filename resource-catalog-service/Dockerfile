FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY resource-catalog-service ./resource-catalog-service
RUN mvn -pl resource-catalog-service -am -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/resource-catalog-service/target/resource-catalog-service-0.0.1-SNAPSHOT.jar ./app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
