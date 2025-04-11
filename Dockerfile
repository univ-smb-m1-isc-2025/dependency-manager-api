FROM eclipse-temurin:21-jre-alpine

COPY ./target/dependency-manager-api-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["sh","-c","java -XX:InitialRAMPercentage=50 -XX:MaxRAMPercentage=70  -XshowSettings $JAVA_OPTS -jar dependency-manager-api-0.0.1-SNAPSHOT.jar"]
