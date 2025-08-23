# Build stage
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy the parent POM and full source code for correct Maven context
COPY . /app

# Install dependencies without running tests
RUN mvn install -DskipTests

# Cache Maven dependencies specific to the submodule
WORKDIR /app/eureka-service-registry
RUN mvn dependency:go-offline

# Build the submodule
COPY ./eureka-service-registry/src /app/eureka-service-registry/src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine-3.21
WORKDIR /app

# Copy the built jar file
COPY --from=build /app/eureka-service-registry/target/*.jar app.jar

EXPOSE 8761
CMD ["java", "-jar", "app.jar"]
