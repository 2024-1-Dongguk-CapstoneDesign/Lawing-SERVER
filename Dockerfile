# jdk17 Image Start
FROM openjdk:17
LABEL authors = "chaen"

# 인자 설정 - JAR_File
ARG JAR_FILE=build/libs/*.jar

# jar 파일 복제
COPY ${JAR_FILE} app.jar

COPY src/main/resources/application.yml .
COPY src/main/resources/application-real.yml .
COPY src/main/resources/application-jwt.yml .
COPY src/main/resources/application-codef.yml .

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=real,jwt,codef

# Expose the port your application runs on
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar","-Duser.timezone=Asia/Seoul", "/app.jar"]