# Use a multi-stage build for a smaller and more secure final image

# --- Build Stage ---
# This stage builds the application using Maven and the full JDK.
FROM eclipse-temurin:17-jdk-jammy as build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom file to download dependencies first
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download all project dependencies (this is cached by Docker to speed up future builds)
RUN ./mvnw dependency:go-offline

# Copy the rest of your application's source code
COPY src ./src

# Package the application into a JAR file, skipping tests for faster builds.
RUN ./mvnw package -DskipTests


# --- Run Stage ---
# This stage uses a smaller Java Runtime Environment (JRE) since we only need to run the app, not build it.
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Copy only the built JAR file from the 'build' stage into this final image
COPY --from=build /app/target/shoptcg-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port that Render will use to communicate with your application
EXPOSE 10000

# The command that runs when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
