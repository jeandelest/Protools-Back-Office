FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD target/flowableDemo-0.0.2.jar flowableDemo-0.0.2.jar
ENTRYPOINT ["java","-jar","/flowableDemo-0.0.2.jar"]
