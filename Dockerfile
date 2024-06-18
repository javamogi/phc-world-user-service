FROM openjdk:17-ea-11-jdk-slim
ENV TZ="Asia/Seoul"
VOLUME /tmp
COPY build/libs/phc-world-user-service-1.0.jar UserService.jar
ENTRYPOINT ["java","-jar","UserService.jar"]