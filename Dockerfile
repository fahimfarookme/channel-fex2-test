FROM adoptopenjdk/openjdk11:ubi
MAINTAINER du
COPY target/channel-flex2-test-0.0.1-SNAPSHOT.jar channel-flex2-test-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-Dserver.port=8889", "-jar","/channel-flex2-test-0.0.1-SNAPSHOT.jar"]
