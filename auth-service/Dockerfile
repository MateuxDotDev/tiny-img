FROM quay.io/quarkus/ubi-quarkus-graalvmce-builder-image:22.3-java17 AS build

WORKDIR /usr/src/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw -B dependency:go-offline

COPY src src

RUN ./mvnw package -Dnative

FROM quay.io/quarkus/quarkus-micro-image:2.0 as runtime

WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root --from=build /usr/src/app/target/*-runner /work/application

EXPOSE 8080
USER 1001

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]
