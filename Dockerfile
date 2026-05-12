# ===== Stage 1: Build with Gradle caching =====
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Copy only Gradle files first so dependencies can be cached
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# Download dependencies (cached)
#####RUN ./gradlew dependencies --no-daemon || true

# resolve dependencies explicitly (caches better sometimes)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon || true

# Copy .git so gradle-git-properties can read commit metadata
COPY .git/ .git/

# Now copy the actual source code
COPY src/ src/

# Build — mount the same cache dir so Gradle reuses downloaded deps/plugins
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew clean bootJar --no-daemon

## Build the jar
#RUN ./gradlew clean bootJar --no-daemon

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built jar from the first stage

COPY --from=build /app/build/libs/northwindApiServer-*jar /app/northwindApiServer.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/northwindApiServer.jar"]

#                -javaagent:/usr/local/javaagent.jar=8088:/usr/local/prometheus-jmx-config.yaml $JAVA_OPTS \