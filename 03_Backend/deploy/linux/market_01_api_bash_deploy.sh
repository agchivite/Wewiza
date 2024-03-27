#!/bin/bash

sudo docker network create public-wewiza-network

cd ../../market_01
sudo docker compose down
sudo docker rmi -f market_01-api_market_01
sudo docker compose up -d