#!/bin/sh

DEV_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DEV_DIR"

./kafka-stop.sh

find ./kafka/data -mindepth 1 -maxdepth 1 ! -name '.gitkeep' -exec rm -r {} \;
find ./kafka/zookeeper -mindepth 2 -maxdepth 2 ! -name '.gitkeep' -exec rm -r {} \;

echo "Done"
