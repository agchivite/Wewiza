from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from src.web_data_collector.simulation_mercadona import SimulationMercadona

# DataBase
# mongo_market_01
connection_mongo = "mongodb://root:root@localhost:27021"

# Servicio Chrome
service = Service()
options = webdriver.ChromeOptions()
driver_chrome = webdriver.Chrome(service=service, options=options)

simulation = SimulationMercadona(driver_chrome, connection_mongo)
simulation.run_simulation(40, 45)
