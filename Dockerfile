FROM openjdk:8-alpine

COPY target/uberjar/ouija.jar /ouija/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/ouija/app.jar"]
