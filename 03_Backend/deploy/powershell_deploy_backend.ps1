
docker network create public-wewiza-network

Set-Location .\03_Backend\deploy

$scriptPath = Get-Location

$script1 = Join-Path $scriptPath "windows\powershell_deploy_api_market_01.ps1"
$script2 = Join-Path $scriptPath "windows\powershell_deploy_api_market_02.ps1"
$script3 = Join-Path $scriptPath "windows\powershell_deploy_api_wewiza.ps1"

Start-Process powershell.exe -ArgumentList "-File $script1"
Start-Process powershell.exe -ArgumentList "-File $script2"
Start-Process powershell.exe -ArgumentList "-File $script3"
