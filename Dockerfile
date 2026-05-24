FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
ENV MAVEN_OPTS="-XX:+UseSerialGC -Xms64m -Xmx512m -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.pool=false -Dhttp.keepAlive=false"
COPY mvnw mvnw.cmd* ./
COPY .mvn .mvn
COPY pom.xml .
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw dependency:go-offline -q
COPY src ./src
RUN ./mvnw package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine AS runtime
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "app.jar"]
