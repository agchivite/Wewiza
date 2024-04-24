from fastapi import FastAPI, Request
from api_market_02.src.services.product_service import ProductService
from api_market_02.src.database.database_manager import DatabaseManager
from api_market_02.src.repositories.product_repository import ProductRepository
import requests
import json

app = FastAPI()

# TODO: Swap in configuration if we are in docker
CONNECTION_MONGO = "mongodb://root:root@mongo_market_02:27017"
DATABASE_NAME = "ahorramas"
COLLECTION_NAME = "products"

# TODO: Dependency Injection with IoC
# Fachade
database_manager = DatabaseManager(CONNECTION_MONGO, DATABASE_NAME)
product_repository = ProductRepository(database_manager, COLLECTION_NAME)
product_service = ProductService(product_repository)


@app.get("/products")
def get_all_products():
    return product_service.get_all_products()


@app.post("/insert_new_scrapped_product")
async def insert_new_scrapped_product(request: Request):
    data = await request.json()
    result = product_service.create_product_to_mongo_recieving_json(data)
    return {"result": result}


@app.get("/products/{category_id}")
def get_products_by_category(category_id: str):
    return product_service.get_all_products_by_category_id(category_id)
