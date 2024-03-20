Set-Location .\03_Backend\market_02
docker-compose down
docker rmi -f market_02-api_market_02
docker-compose up