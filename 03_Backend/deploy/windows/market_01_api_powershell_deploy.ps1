# Ruta completa del directorio del script actual
$scriptDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path

$marketDirectory = Join-Path $scriptDirectory "..\..\market_01"
Set-Location $marketDirectory

docker-compose down
docker rmi -f market_01-api_market_01
docker-compose up
