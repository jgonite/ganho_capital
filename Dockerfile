# Etapa 1: Build - baixar o maven e empacotar o executável .jar da aplicação.
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Execução - mover o 
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/ganho-capital-1.0.0.jar ./ganho-capital.jar
ENTRYPOINT ["java", "-jar", "ganho-capital.jar"]