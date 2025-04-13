FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY build/libs/manasnap-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS="-Xmx512m -Xms256m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]