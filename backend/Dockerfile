FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y git curl unzip maven && \
    curl -o /tmp/sonar-scanner.zip https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-6.2.1.4610-linux-x64.zip && \
    unzip /tmp/sonar-scanner.zip -d /opt && \
    mv /opt/sonar-scanner-6.2.1.4610-linux-x64 /opt/sonar-scanner && \
    rm /tmp/sonar-scanner.zip && \
    apt-get remove -y unzip && \
    rm -rf /var/lib/apt/lists/*

ENV PATH="/opt/sonar-scanner/bin:$PATH"

COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=produccion", "app.jar"]