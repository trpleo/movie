# we will use openjdk 8 with alpine as it is a very small linux distro
# FROM openjdk:8-jre-alpine3.9
FROM debian

ENV DEBIAN_FRONTEND=noninteractive

RUN apt update && apt upgrade && apt install curl -y

WORKDIR /opt
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
    && curl \
        -L \
        -o openjdk.tar.gz \
        https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.1_linux-x64_bin.tar.gz \
    && mkdir jdk \
    && tar zxf openjdk.tar.gz -C jdk --strip-components=1 \
    && rm -rf openjdk.tar.gz \
    && apt-get -y --purge autoremove curl \
    && ln -sf /opt/jdk/bin/* /usr/local/bin/ \
    && rm -rf /var/lib/apt/lists/* \
    && java  --version \
    && javac --version \
    && jlink --version

# copy the packaged jar file into our docker image
COPY ./build/libs/movie-all.jar /movie.jar

# set the startup command to execute the jar
# CMD ["java", "-jar", "/movie.jar"]
ENTRYPOINT exec java $JAVA_OPTS  -jar /movie.jar