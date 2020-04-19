FROM openjdk:11
WORKDIR /usr/app
COPY build/libs/BBC-1.0-SNAPSHOT-all.jar ./app.jar

CMD java -jar app.jar