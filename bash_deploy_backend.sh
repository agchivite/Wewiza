#!/bin/bash

powershell.exe -File ./03_Backend/deploy/linux/bash_deploy_api_market_01.ps1
powershell.exe -File ./03_Backend/linux/bash_deploy_api_market_02.ps1
powershell.exe -File ./03_Backend/linux/bash_deploy_api_wewiza.ps1
