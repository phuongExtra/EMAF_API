#!/bin/bash

# Stop bash when failer occur
set -e

# Constant
INSTANCE_NAME="emaf-api"
PORT=5100
STG_ENV="stg"
PROD_ENV="prod"

read -p "Build branch: " branch
read -p "Build env (stg/prod): " env

if [ $env == $STG_ENV ]
then
  PORT=5100
elif [ $env == $PROD_ENV ]
then
  PORT=5200
else
  echo "[ERROR] env must be stg or prod"
  exit 1
fi

# Script
START_TIME=$(date +%s)
echo "\n+++ Start +++"

echo "\n-- Switch to ${branch}"
git fetch
git checkout $branch

echo "\n-- Pulling from ${branch}..."
git pull

echo "\n-- Installing..."
rm -rf build
gradle clean build -x test

echo "\n-- Building at [${env}] environment, the application will run on ${PORT}"
fuser -k $PORT/tcp
nohup java -jar -Dspring.profiles.active=$env -Dserver.port=$PORT build/libs/$INSTANCE_NAME-0.0.1-SNAPSHOT.jar &

END_TIME=$(date +%s)
EXEC_TIME=$(($END_TIME-$START_TIME))
echo "\n\n+++ Finish at $EXEC_TIME seconds +++\n"

