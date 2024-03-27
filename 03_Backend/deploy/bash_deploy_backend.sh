#!/bin/bash

sudo docker network create public-wewiza-network

bash ./linux/bash_deploy_api_market_01.sh
bash ./linux/bash_deploy_api_market_02.sh
bash ./linux/bash_deploy_api_wewiza.sh
