FROM maven:3.6.0-jdk-8-alpine as builder
COPY . .
# Here we skip tests to save time, because if this image is being built - tests have already passed...
RUN mvn install -DskipTests

FROM openjdk:8-jre-alpine

COPY --from=builder /target/s3mq-*-jar-with-dependencies.jar s3mq.jar

CMD ["java", "-jar", "s3mq.jar", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap"]
