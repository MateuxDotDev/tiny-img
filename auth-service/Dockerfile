FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /usr/src/app

COPY pom.xml .

RUN mvn -B dependency:go-offline

COPY src src

RUN mvn -Pnative clean package

FROM quay.io/quarkus/quarkus-micro-image:2.0 as runtime

WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root --from=build /usr/src/app/target/*-runner /work/application

EXPOSE 8080
USER 1001

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]
