#!/bin/sh

DEV_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DEV_DIR"

docker-compose -f kafka-stack.yaml -p fpdev_kafka exec kafka1 kafka-topics --create --zookeeper zoo1:2181 \
    --replication-factor 1 \
    --partitions 1 \
    --topic new

docker-compose -f kafka-stack.yaml -p fpdev_kafka exec kafka1 kafka-topics --create --zookeeper zoo1:2181 \
    --replication-factor 1 \
    --partitions 1 \
    --topic delete

docker-compose -f kafka-stack.yaml -p fpdev_kafka exec kafka1 kafka-topics --create --zookeeper zoo1:2181 \
    --replication-factor 1 \
    --partitions 1 \
    --topic modify

echo "Done"
