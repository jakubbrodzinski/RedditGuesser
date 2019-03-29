FROM openjdk:10-jdk-oracle
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /ml_webapp/lib
COPY ${DEPENDENCY}/META-INF /ml_webapp/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /ml_webapp
ENTRYPOINT ["java","-cp","ml_webapp:ml_webapp/lib/*","bach.project.configuration.Application"]
