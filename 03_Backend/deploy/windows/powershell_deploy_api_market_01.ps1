Set-Location .\03_Backend\market_01
docker-compose down
docker rmi -f market_01-api_market_01
docker-compose up