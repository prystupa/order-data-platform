#!/bin/sh

set -e

ec2-run-instances ami-4c1c8d24 --instance-type m3.medium --group hazelcast-cluster \
    --instance-count ${N-2} --user-data-file user-data-client.txt \
    --key sybase-iq \
    | grep INSTANCE | cut -f 2 \
    | ec2-create-tags - --tag role=event-capture
