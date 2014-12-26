#!/bin/sh

set -e

# start Ambari server using hadoop-ambari AMI
ec2-run-instances ami-46c4aa2e \
    --instance-type ${I-m3.xlarge} \
    --group hadoop-cluster \
    --key hadoop \
    | grep INSTANCE | cut -f 2 \
    | ec2-create-tags - --tag role=hadoop-ambari

# start Hadoop nodes using hadoop-base AMI
ec2-run-instances ami-52c7a93a \
    --instance-type ${I-m3.xlarge} \
    --instance-count ${N-6} \
    --group hadoop-cluster \
    --key hadoop \
    | grep INSTANCE | cut -f 2 \
    | ec2-create-tags - --tag role=hadoop-node
