FROM openjdk:8-jre-alpine

COPY target/s3mq-*.jar s3mq.jar

CMD ["java", "-jar", "s3mq.jar", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap"]
