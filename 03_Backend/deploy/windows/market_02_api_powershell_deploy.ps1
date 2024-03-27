# Ruta completa del directorio del script actual
$scriptDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path

$marketDirectory = Join-Path $scriptDirectory "..\..\market_02"
Set-Location $marketDirectory

docker-compose down
docker rmi -f market_02-api_market_02
docker-compose up