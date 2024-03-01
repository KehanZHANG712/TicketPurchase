# build stage
FROM maven:3.9-amazoncorretto-17 AS build

WORKDIR /app

COPY . .
RUN mvn clean install

# run stage
FROM tomcat:10.1.12

COPY --from=build /app/target/WZ-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/wz2.1.war