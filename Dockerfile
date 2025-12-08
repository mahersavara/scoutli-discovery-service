####
# This Dockerfile is used in order to build a container that runs the Quarkus application in JVM mode
####
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /build

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn package -DskipTests

####
# Build the runtime image
####
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built artifact from build stage
COPY --from=build /build/target/quarkus-app/lib/ /app/lib/
COPY --from=build /build/target/quarkus-app/*.jar /app/
COPY --from=build /build/target/quarkus-app/app/ /app/app/
COPY --from=build /build/target/quarkus-app/quarkus/ /app/quarkus/

# Set the entrypoint
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
