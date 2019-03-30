FROM openjdk:10-jdk-oracle
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /ml_webapp/lib
COPY ${DEPENDENCY}/META-INF /ml_webapp/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /ml_webapp

EXPOSE 8080/tcp

ADD run.sh run.sh
RUN chmod a+x run.sh

CMD ./run.sh

