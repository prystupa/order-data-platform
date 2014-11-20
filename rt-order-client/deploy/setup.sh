#!/bin/sh
# Abort our setup if anything goes wrong.
set -e

yum install -y -q java-1.7.0-openjdk-devel

java -Daws.access-key=$AWS_ACCESS_KEY -Daws.secret-key=$AWS_SECRET_KEY -cp './*' com.prystupa.client.SimulatedLoadApp
