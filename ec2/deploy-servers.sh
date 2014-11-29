#!/bin/sh

set -e

ec2-run-instances ami-5613813e --instance-type ${I-m3.xlarge} --availability-zone ${Z-us-east-1c} --group hazelcast-cluster \
    --instance-count ${N-2} --user-data-file user-data-server.txt \
    --key sybase-iq \
    | grep INSTANCE | cut -f 2 \
    | ec2-create-tags - --tag role=hz-server
