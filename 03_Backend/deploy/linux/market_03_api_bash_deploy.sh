#!/bin/bash

sudo docker network create public-wewiza-network

cd ../../market_03
sudo docker compose down
sudo docker rmi -f market_03-api_market_03
sudo docker compose up -d