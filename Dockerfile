FROM frolvlad/alpine-oraclejdk8:slim

WORKDIR /App

ADD target/callcenter-almundo-0.0.1.jar app.jar
ADD /entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT ["/entrypoint.sh"]