# Multiple execution scripts by PowerShell

#docker network create public-wewiza-network

$script1 = "./03_Backend/powershell_deploy_api_market_01.ps1"
$script2 = "./03_Backend/powershell_deploy_api_market_02.ps1"
$script3 = "./03_Backend/powershell_deploy_api_wewiza.ps1"

Start-Process powershell.exe -ArgumentList "-File $script1"
Start-Process powershell.exe -ArgumentList "-File $script2"
Start-Process powershell.exe -ArgumentList "-File $script3"
