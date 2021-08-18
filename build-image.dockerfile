FROM openjdk:8u242-jre

COPY ./target/scala-2.13/*.jar .
COPY ./src/main/resources/application.conf .
RUN mv ./*.jar ./app.jar

CMD	java -Dconfig.file=./application.conf -jar ./app.jar
