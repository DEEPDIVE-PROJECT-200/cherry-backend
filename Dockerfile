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

# Build args for dev (GitHub Actions에서 주입 후 이미지 완성되면 사라짐)
ARG JWT_SECRET
ARG ACCESS_TOKEN_EXPIRATION_SECONDS
ARG REFRESH_TOKEN_EXPIRATION_SECONDS
ARG KAKAO_CLIENT_ID
ARG KAKAO_REDIRECT_URL

# Dev environment variables (런타임, 이미지에 포함)
ENV JWT_SECRET=${JWT_SECRET}
ENV ACCESS_TOKEN_EXPIRATION_SECONDS=${ACCESS_TOKEN_EXPIRATION_SECONDS}
ENV REFRESH_TOKEN_EXPIRATION_SECONDS=${REFRESH_TOKEN_EXPIRATION_SECONDS}
ENV KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID}
ENV KAKAO_REDIRECT_URL=${KAKAO_REDIRECT_URL}

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