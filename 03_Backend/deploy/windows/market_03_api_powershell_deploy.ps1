# Ruta completa del directorio del script actual
$scriptDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path

$marketDirectory = Join-Path $scriptDirectory "..\..\market_03"
Set-Location $marketDirectory

docker-compose down
docker rmi -f market_03-api_market_03
docker-compose up