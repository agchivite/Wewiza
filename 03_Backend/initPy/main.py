from selenium import webdriver
from selenium.webdriver.chrome.service import Service

from utils.SimulationDia import SimulationDia
from utils.SimulationEroski import SimulationEroski
from utils.SimulationMercadona import SimulationMercadona

# DataBase 
connection_mongo = "mongodb://localhost:27017"

# Servicio Chrome
service = Service()
options = webdriver.ChromeOptions()
driver_chrome = webdriver.Chrome(service=service, options=options)
driver = webdriver.Chrome()

#simulationMercadona = SimulationMercadona(driver_chrome, connection_mongo)
#simulationMercadona.run_simulation(40, 45)

#simulationDia = SimulationDia(driver, connection_mongo)
#simulationDia.run_simulation()
#print(simulationDia.scrape_categorias())

simulationEroski = SimulationEroski(driver, connection_mongo)
#simulationDia.run_simulation()
main_urls_list = simulationEroski.scrappe_main_categorias()
sub_categories_list = simulationEroski.scrappe_sub_categories(main_urls_list)
simulationEroski.run_simulation(sub_categories_list)