# --- Stage 1: Build ---
# Gradle로 JAR 파일을 빌드하는 단계
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# 의존성 캐시 최적화: gradle 설정 파일을 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
RUN chmod +x ./gradlew
# 의존성만 먼저 다운로드 (소스 변경 없으면 캐시 재사용)
RUN ./gradlew dependencies --no-daemon

# 소스 복사 후 빌드 (테스트 제외 — CI에서 별도 실행)
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# --- Stage 2: Run ---
# JRE만 포함한 경량 이미지로 실행 (JDK 불필요)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 타임존 설정 (한국 표준시)
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apk del tzdata

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx512m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
