#!/bin/bash

# a simple demo script for EBS.
# $1 is the number of lines we're looking for.
# $2 is the regex we wish to test
# $3 is the execution ID we're looking for in elasticsearch
#
# So, an execution example would be:
# ./run.sh 55 "(?s).*WARN.*" 133


REGEXP=$2
EXEC_ID=$3
REQUESTED_COUNT=$1
mvn package >/devnull 2>&1


COUNT=$(spark-submit --class org.sparkexample.WordCountTask --master spark://sparkmaster:7077 --conf spark.es.nodes="elastest_edm-elasticsearch_1" /demo-projects/ebs-test/target/hadoopWordCount-1.0-SNAPSHOT.jar $REGEXP $EXEC_ID 2>/dev/null |grep list_size | awk '{print $2}')

if [ $COUNT -lt $REQUESTED_COUNT ]; then
	echo below threshold: counted $COUNT lines on a $REQUESTED_COUNT threshold
	exit 0
else
	echo above threshold: counted $COUNT lines on a $REQUESTED_COUNT threshold
	exit 1
fi
