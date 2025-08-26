# Build stage
FROM amazoncorretto:21-alpine-jdk AS builder
WORKDIR /app

# 의존성 관련 파일들만 먼저 복사
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .
COPY gradle gradle

# 의존성 다운로드 (소스 변경과 무관)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies

# 소스코드는 마지막에 복사
COPY src src
RUN ./gradlew bootJar

# Development stage
FROM amazoncorretto:21-alpine-jdk AS development

# 타임존 설정
RUN apk add --no-cache tzdata
ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]

# Production stage
FROM amazoncorretto:21-alpine-jdk AS production

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