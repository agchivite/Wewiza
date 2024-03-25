from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from api_market_01.src.services.scrapping_service import ScrappingService
from api_market_01.src.services.product_service import ProductService
from api_market_01.src.database.database_manager import DatabaseManager
from api_market_01.src.repositories.product_repository import ProductRepository

CONNECTION_MONGO = "mongodb://root:root@localhost:27021"
DATABASE_NAME = "donamerca"
COLLECTION_NAME = "products"

# Chrome Service
service = Service()
options = webdriver.ChromeOptions()
driver_chrome = webdriver.Chrome(service=service, options=options)

# Fachade
database_manager = DatabaseManager(CONNECTION_MONGO, DATABASE_NAME)
product_repository = ProductRepository(database_manager, COLLECTION_NAME)

# Services
product_service = ProductService(product_repository)
scrapping_service = ScrappingService(driver_chrome, product_service)

# Num id_categories are includes when scrapping
start_category = 27
end_category = 244
scrapping_service.run_simulation(start_category, end_category)
