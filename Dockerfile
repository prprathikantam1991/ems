# Use a Java base image
FROM eclipse-temurin:17-jre

# Add a label (optional)
LABEL maintainer="you@example.com"

# Add the JAR file
COPY target/*.jar app.jar

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app.jar"]
