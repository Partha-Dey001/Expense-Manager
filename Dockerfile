FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/expensemanager-0.0.1-SNAPSHOT.jar expensemanager-v1.0.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "expensemanager-v1.0.jar"]