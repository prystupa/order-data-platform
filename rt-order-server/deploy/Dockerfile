FROM java

MAINTAINER "Eugene Prystupa"

RUN wget -q https://github.com/prystupa/rt-order-reporting/releases/download/v0.5/rt-order-server-1.0-SNAPSHOT-bin.tar.gz -O /opt/rt-order-server-1.0-SNAPSHOT-bin.tar.gz

RUN tar -zxf /opt/rt-order-server-1.0-SNAPSHOT-bin.tar.gz -C /opt && mv /opt/target /opt/rt-order-server && rm /opt/rt-order-server-1.0-SNAPSHOT-bin.tar.gz

