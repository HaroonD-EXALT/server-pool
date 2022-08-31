# FROM openjdk:8-jdk-alpine
FROM openjdk:latest
FROM debian:stretch-slim 
FROM gradle:7.5.1-jdk8
FROM python:3.7.7-slim-stretch


ENV AEROSPIKE_VERSION 4.5.0.3
ENV AEROSPIKE_SHA256 baf76fbb822cb8cfc420c18ab789c3d772280736e248dac44f2b225e789d366e
ENV JAVA_HOME /usr/lib/jvm/* #This can vary
ENV PATH $PATH:$JAVA_HOME/bin
# Install Aerospike Server and Tools


RUN \
  apt-get update -y \
  && apt-get install dh-python -y\
  && apt-get install -y wget python lua5.2 gettext-base \
  && wget "https://www.aerospike.com/artifacts/aerospike-server-community/${AEROSPIKE_VERSION}/aerospike-server-community-${AEROSPIKE_VERSION}-debian9.tgz" -O aerospike-server.tgz \
  && echo "$AEROSPIKE_SHA256 *aerospike-server.tgz" | sha256sum -c - \
  && mkdir aerospike \
  && tar xzf aerospike-server.tgz --strip-components=1 -C aerospike \
  && dpkg -i aerospike/aerospike-server-*.deb \
  && dpkg -i aerospike/aerospike-tools-*.deb \
  && mkdir -p /var/log/aerospike/ \
  && mkdir -p /var/run/aerospike/ \
  && rm -rf aerospike-server.tgz aerospike /var/lib/apt/lists/* \
  && rm -rf /opt/aerospike/lib/java \
  && dpkg -r wget ca-certificates openssl xz-utils\
  && dpkg --purge wget ca-certificates openssl xz-utils\
  && apt-get purge -y \
  && apt autoremove -y 

  


# Add the Aerospike configuration specific to this dockerfile
COPY aerospike.template.conf /etc/aerospike/aerospike.template.conf
COPY entrypoint.sh /entrypoint.sh
# Mount the Aerospike data directory
VOLUME ["/opt/aerospike/data"]
# VOLUME ["/etc/aerospike/"]


# Expose Aerospike ports
#
#   3000 – service port, for client connections
#   3001 – fabric port, for cluster communication
#   3002 – mesh port, for cluster heartbeat
#   3003 – info port
#
EXPOSE 3000 3001 3002 3003

# Execute the run script in foreground mode
# ENTRYPOINT ["/entrypoint.sh"]
WORKDIR /app
VOLUME /tmp
COPY . WORKDIR

# RUN javac /app/src/main/java/com/project/serverpool/ServerPoolApplication.java
CMD ["java", "ServerPoolApplication"]
# RUN gradle bootRun

# ENTRYPOINT  java -jar WORKDIR/build/libs/server-pool.jar
# ENTRYPOINT ["java", "-jar", "${WORKDIR}/build/libs/server-pool.jar"]


# COPY build/libs/server-pool.jar server-pool.jar
# #A/DD build/libs/server-pool.jar server-pool.jar
# ENTRYPOINT ["java","-jar","/server-pool.jar"]
# COPY build/libs/server-pool.jar server-pool.jar
# #A/DD build/libs/server-pool.jar server-pool.jar
# ENTRYPOINT ["java","-jar","/server-pool.jar"]