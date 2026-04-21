FROM cgr.dev/chainguard-private/jdk:openjdk-17-dev
USER root
RUN apk add maven

USER java
WORKDIR /home/build
COPY --chown=65532 . /home/build

RUN mvn clean package

ENTRYPOINT ["java"]
CMD ["-jar", "target/mysql-jdbc-demo.jar"]
