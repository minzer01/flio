# 1단계: Gradle로 jar 빌드
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 의존성 먼저 받아두면 코드만 바뀔 때 빌드가 빨라짐
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null 2>&1 || true

COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# 2단계: 실행용 가벼운 이미지
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
# JAVA_OPTS로 메모리 상한 조절 (프리티어 서버 대비)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
