# --- Etapa 1: compilar ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src src
# Los tests ya corren en el CI; acá solo se arma el jar
RUN ./mvnw -B -DskipTests package

# --- Etapa 2: correr ---
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]