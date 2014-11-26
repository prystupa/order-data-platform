#!/bin/sh
# Abort our setup if anything goes wrong.
set -e

yum install -y -q wget

pushd /opt
wget --quiet --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u25-b17/jdk-8u25-linux-x64.tar.gz"
tar xzf jdk-8u25-linux-x64.tar.gz

cd /opt/jdk1.8.0_25/
alternatives --install /usr/bin/java java /opt/jdk1.8.0_25/bin/java 2

popd
java $JAVA_OPTS -Daws.enabled=true -Daws.access-key=$AWS_ACCESS_KEY -Daws.secret-key=$AWS_SECRET_KEY \
    -cp './*' com.prystupa.client.SimulateLoadApp -n=${RT_SIMULATOR_EVENTS:-1}
