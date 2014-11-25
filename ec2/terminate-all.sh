#!/bin/sh

set -e

ec2-describe-instances \
    | grep INSTANCE | grep running | cut -f 2 \
    | ec2-terminate-instances -