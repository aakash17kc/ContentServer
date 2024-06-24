# Dockerfile to build the image for the content server
FROM maven:3.8.6-openjdk-17 as builder
WORKDIR /app
COPY . .
RUN mvn package -DskipTests

# Final stage
FROM amazoncorretto:21.0.3
WORKDIR /app
COPY --from=builder /app/target/*.jar content_server.jar
CMD ["java", "-jar", "content_server.jar"]
