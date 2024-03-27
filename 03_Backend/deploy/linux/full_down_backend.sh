#!/bin/bash

cd ../../market_01
sudo docker compose down
cd ../market_02
sudo docker compose down
cd ../wewiza
sudo docker compose down