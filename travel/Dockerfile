# 기본 이미지 설정
FROM maven:3.6.3-openjdk-11-slim AS build

# 작업 디렉토리 설정
WORKDIR /app

# 소스 코드와 pom.xml 파일 복사
COPY . /app

# Maven 빌드 수행
RUN mvn -f /app/pom.xml clean package -DskipTests

# 런타임 이미지
FROM openjdk:11-jre-slim

# 빌드 이미지에서 빌드 결과인 JAR 파일 복사
COPY --from=build /app/target/*.jar /app.jar

# 컨테이너 포트 설정
EXPOSE 8080

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]