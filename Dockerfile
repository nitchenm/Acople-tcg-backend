# Use a multi-stage build for a smaller and more secure final image

# Use a multi-stage build for a smaller and more secure final image

# --- Build Stage ---
# CHANGED: We use a base image that already has Maven installed.
# This avoids the "missing .mvn" error because we don't need the wrapper files.
FROM maven:3.9-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# CHANGED: We only copy the pom.xml. We do NOT copy .mvn or mvnw anymore.
COPY pom.xml .

# Download all project dependencies
# CHANGED: We use 'mvn' directly instead of './mvnw'
RUN mvn dependency:go-offline

# Copy the rest of your application's source code
COPY src ./src

# Package the application into a JAR file, skipping tests for faster builds.
# CHANGED: Using 'mvn' command
RUN mvn clean package -DskipTests


# --- Run Stage ---
# This stage uses a smaller Java Runtime Environment (JRE) since we only need to run the app.
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Copy only the built JAR file from the 'build' stage into this final image
# CHANGED: I used *.jar to make it flexible. This way, if you change the version
# in your pom.xml (e.g., 0.0.2), this line will still work without editing.
COPY --from=build /app/target/*.jar app.jar

# Expose the port that Render will use
EXPOSE 10000

# The command that runs when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
