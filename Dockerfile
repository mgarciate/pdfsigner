# Use the official maven/Java 8 image to create a build artifact.
FROM maven:3.6-jdk-8 AS build

WORKDIR /app

COPY pom.xml .

# Download the dependencies
RUN mvn dependency:go-offline -B dependency:copy-dependencies -DoutputDirectory=target/dependencies

COPY src ./src

# Build a release artifact
RUN mvn package -DskipTests

# Use OpenJDK JRE for the runtime
FROM openjdk:8-jre-slim

WORKDIR /app

# Copy the jar to the production image from the builder stage
COPY --from=build /app/target/dependencies/*.jar /app/
COPY --from=build /app/target/*.jar /app/pdfsigner.jar

# Command to run the application
CMD ["java", "-jar", "/app/pdfsigner.jar"]