
docker network create public-wewiza-network

Set-Location .\03_Backend\deploy\windows

$scriptPath = Get-Location

$script1 = Join-Path $scriptPath "market_01_api_powershell_deploy.ps1"
$script2 = Join-Path $scriptPath "market_02_api_powershell_deploy.ps1"
$script3 = Join-Path $scriptPath "market_03_api_powershell_deploy.ps1"
$script4 = Join-Path $scriptPath "wewiza_api_powershell_deploy.ps1"

Start-Process powershell.exe -ArgumentList "-File $script1"
Start-Process powershell.exe -ArgumentList "-File $script2"
Start-Process powershell.exe -ArgumentList "-File $script3"
Start-Process powershell.exe -ArgumentList "-File $script4"
