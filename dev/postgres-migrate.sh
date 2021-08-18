#!/bin/bash

DEV_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DEV_DIR"

# https://stackoverflow.com/questions/31324981/how-to-access-host-port-from-docker-container/43541732#43541732
docker run --rm -v $DEV_DIR/../migrations:/flyway/sql flyway/flyway:7.8.1 \
 -url=jdbc:postgresql://host.docker.internal:5432/app -user=postgres -password=example migrate
