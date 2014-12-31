#!/bin/bash
# Abort our setup if anything goes wrong.
set -e

LIBJARS=lib/`ls -m lib | tr -d '\n' | tr -d ' ' | sed -e 's/,/,lib\//g'`
HADOOP_CLASSPATH=lib/`ls -m lib | tr -d '\n' | tr -d ' ' | sed -e 's/,/:lib\//g'`

export HADOOP_CLASSPATH
hadoop jar hd-event-generator-1.0-SNAPSHOT.jar com.prystupa.generate.OrderEventCountApp \
    -libjars=$LIBJARS  \
    -D verbose=${VERBOSE:-false} \
    ${IN:-events/`date "+%Y-%m-%d"`}
