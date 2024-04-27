#!/bin/bash

sudo docker network create public-wewiza-network

bash market_01_api_bash_deploy.sh
bash market_02_api_bash_deploy.sh
bash market_03_api_bash_deploy.sh
bash wewiza_api_bash_deploy.sh
