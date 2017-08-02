FROM openjdk:8-jdk-alpine
COPY target/huey-standalone.jar huey-standalone.jar
ENV PORT 3000
EXPOSE 3000
CMD ["java", "-jar", "huey-standalone.jar"]
