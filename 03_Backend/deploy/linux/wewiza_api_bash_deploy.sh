#!/bin/bash

sudo docker network create public-wewiza-network

cd ../../wewiza
sudo docker compose down
sudo docker rmi -f wewiza-api_wewiza
sudo docker compose up -d