# Build stage
FROM eclipse-temurin:17-jdk-focal as build
WORKDIR /workspace/app

# Copiar arquivos do projeto
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Garantir que mvnw seja executável
RUN chmod +x mvnw
# Build do projeto
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copiar o jar do estágio de build
COPY --from=build /workspace/app/target/*.jar app.jar

# Expor a porta da aplicação
EXPOSE 8081

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]