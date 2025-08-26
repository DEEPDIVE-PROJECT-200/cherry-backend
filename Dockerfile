# Build stage
FROM openjdk:21-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# Development stage
FROM openjdk:21-jre-alpine AS development

# 타임존 설정
RUN apk add --no-cache tzdata
ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]

# Production stage
FROM openjdk:21-jre-alpine AS production

# 타임존 설정
RUN apk add --no-cache tzdata
ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

WORKDIR /app

# Production은 런타임 환경변수만 (K8s에서 주입)
# ARG, ENV 시크릿 관련 설정 없음

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-Dspring.profiles.active=prod", "-jar", "app.jar"]