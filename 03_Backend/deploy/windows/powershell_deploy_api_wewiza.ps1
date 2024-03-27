# Ruta completa del directorio del script actual
$scriptDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path

$marketDirectory = Join-Path $scriptDirectory "..\..\wewiza"
Set-Location $marketDirectory

docker-compose down
docker rmi -f wewiza-api_wewiza
docker-compose up