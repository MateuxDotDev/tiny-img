FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 AS build
USER quarkus

WORKDIR /code

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw -B dependency:go-offline

COPY src /code/src

RUN ./mvnw package -Dnative -DskipTests

FROM quay.io/quarkus/quarkus-micro-image:2.0

WORKDIR /work/
COPY --from=build /code/target/*-runner /work/application
RUN chmod 775 /work

USER quarkus
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
