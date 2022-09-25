FROM camunda/zeebe-simple-monitor:0.17.0-alpha1 as source

FROM openjdk:11-jre

COPY build/libs/rendr-0.0.1-SNAPSHOT.jar rendr-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/rendr-0.0.1-SNAPSHOT.jar"]