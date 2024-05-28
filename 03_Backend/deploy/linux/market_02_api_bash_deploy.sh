#!/bin/bash

sudo docker network create public-wewiza-network

cd ../../market_02
sudo docker compose down
sudo docker rmi -f market_02-api_market_02
sudo docker compose up -d