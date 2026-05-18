# 1. 자바 실행 환경(JRE) 이미지 지정
FROM eclipse-temurin:21-jre-alpine

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너 내부로 복사
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 4. 스프링 부트 서버 실행 명령 (Cloud Run이 주입하는 PORT 환경변수 반영)
ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-jar", "/app/app.jar"]