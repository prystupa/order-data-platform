#!/bin/bash

ec2-describe-instances --filter "tag-value=hadoop-node" | grep running | cut -f 5
