#!/bin/bash

DEV_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DEV_DIR"

docker-compose -p fpdev_postgres -f postgres.yaml up -d
