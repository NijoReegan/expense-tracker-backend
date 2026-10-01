# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Stage 1 - build
# Uses the Maven wrapper (./mvnw, pinned to Maven 3.9.16 in
# .mvn/wrapper/maven-wrapper.properties) so the container build stays in sync
# with local and CI builds.
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace

# Resolve dependencies first, as its own layer. Docker caches it until
# pom.xml actually changes, so editing Java code does not re-download the
# whole dependency tree.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

COPY src/ src/

# Tests are skipped on purpose: ExpenseTrackerApplicationTests.contextLoads boots
# the full Spring context and needs JWT_SECRET plus a reachable database, neither
# of which exist during an image build. Run them in CI against a real database.
RUN ./mvnw -B -ntp clean package -DskipTests

# Fail fast with a clear message if the packaging step ever changes the
# artifact name, instead of failing later with "no such file".
RUN test -f target/et-server-0.0.1-SNAPSHOT.jar

# ---------------------------------------------------------------------------
# Stage 2 - runtime
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime

# Deterministic logs regardless of host timezone.
ENV TZ=UTC

# Let the JVM size its heap from the container memory limit. Render's free and
# starter instances only have 512 MB, and without this the JVM guesses from host
# memory and gets OOM-killed.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"

# Unprivileged runtime user.
RUN groupadd --system --gid 1001 spring \
    && useradd --system --uid 1001 --gid spring --home-dir /app --shell /usr/sbin/nologin spring

WORKDIR /app

# Only the fat jar is carried forward - no sources, no Maven, no wrapper.
COPY --from=build --chown=spring:spring /workspace/target/et-server-0.0.1-SNAPSHOT.jar app.jar

USER spring:spring

# server.port already reads ${PORT:8089} in application.properties, and Render
# injects PORT, so the container follows whatever port Render assigns.
EXPOSE 8089

# Spring Boot registers a shutdown hook, so Render's SIGTERM on deploy triggers
# a graceful stop (see server.shutdown in application.properties).
STOPSIGNAL SIGTERM

# curl already ships in eclipse-temurin:21-jre-jammy, so no apt layer is needed.
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD ["/bin/sh", "-c", "curl -fsS http://127.0.0.1:${PORT:-8089}/health || exit 1"]

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Smaller alternative: Dockerfile.alpine uses the musl-based temurin images
# (~410 MB vs ~530 MB) and is verified to pass the same checks. Swap the FROM
# lines if image size matters more than glibc compatibility.
