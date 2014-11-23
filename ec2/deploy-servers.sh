#!/bin/sh

set -e

ec2-run-instances ami-7c66f414 --instance-type m3.medium --group hazelcast-cluster \
    --instance-count ${N-2} --user-data-file user-data-server.txt \
    --key sybase-iq \
    | grep INSTANCE | cut -f 2 \
    | ec2-create-tags - --tag role=hz-server
