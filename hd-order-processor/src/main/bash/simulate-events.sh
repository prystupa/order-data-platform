#!/bin/bash
# Abort our setup if anything goes wrong.
set -e

LIBJARS=lib/`ls -m lib | sed -e 's/, /,lib\//g' | tr -d '\n'`
hadoop jar hd-order-processor-1.0-SNAPSHOT.jar com.prystupa.generate.OrderEventGeneratorApp \
    -libjars=$LIBJARS  \
    -D event-count=${N:-1000000} \
    -D verbose=${VERBOSE:-false} \
    ${OUT:-events/`date "+%Y-%m-%d"`}
