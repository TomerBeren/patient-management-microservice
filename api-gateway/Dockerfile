# syntax=docker/dockerfile:1.3

###############################################################################
#                            BUILD STAGE                                     #
###############################################################################
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 1) Copy only the POM and download all dependencies (cached)
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -T1C dependency:go-offline -B

# 2) Copy your source and build the JAR (re-uses the same cache)
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -T1C clean package -DskipTests -B

###############################################################################
#                           RUNTIME STAGE                                     #
###############################################################################
FROM openjdk:21-jdk AS runner

WORKDIR /app

# Copy the fat JAR from the builder
COPY --from=builder /app/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 4004

ENTRYPOINT ["java", "-jar", "app.jar"]
