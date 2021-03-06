#!/bin/bash
# Abort our setup if anything goes wrong.
set -e

LIBJARS=lib/`ls -m lib | tr -d '\n' | tr -d ' ' | sed -e 's/,/,lib\//g'`
HADOOP_CLASSPATH=lib/`ls -m lib | tr -d '\n' | tr -d ' ' | sed -e 's/,/:lib\//g'`

export HADOOP_CLASSPATH
hadoop jar hd-event-generator-1.0-SNAPSHOT.jar com.prystupa.generate.OrderEventGeneratorApp \
    -libjars=$LIBJARS  \
    -D event-count=${N:-1000000} \
    -D verbose=${VERBOSE:-false} \
    ${OUT:-events/`date "+%Y-%m-%d"`}
