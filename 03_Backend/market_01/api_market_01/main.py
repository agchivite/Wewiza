from fastapi import FastAPI, Request
from api_market_01.src.services.product_service import ProductService
from api_market_01.src.database.database_manager import DatabaseManager
from api_market_01.src.repositories.product_repository import ProductRepository
import requests
import json

app = FastAPI()

# TODO: Swap in configuration if we are in docker
CONNECTION_MONGO = "mongodb://root:root@mongo_market_01:27017"
DATABASE_NAME = "mercadona"
COLLECTION_NAME = "products"

# TODO: Dependency Injection with IoC
# Fachade
database_manager = DatabaseManager(CONNECTION_MONGO, DATABASE_NAME)
product_repository = ProductRepository(database_manager, COLLECTION_NAME)
product_service = ProductService(product_repository)


@app.get("/products")
def get_all_products():
    return product_service.get_all_products()


@app.get("/products/market/{market_name}")
def get_all_products_by_market(market_name: str):
    return product_service.get_all_products_by_market(market_name)


@app.post("/insert_new_scrapped_product")
async def insert_new_scrapped_product(request: Request):
    data = await request.json()
    result = product_service.create_product_to_mongo_recieving_json(data)
    print(result)
    return {"result": result}


@app.get("/products/category/id/{category_id}")
def get_products_by_category(category_id: str):
    return product_service.get_all_products_by_category_id(category_id)


@app.get("/size")
def get_size():
    return product_service.get_size()


@app.get("/products/range/{init_num}/{end_num}")
def get_products_by_range(init_num: int, end_num: int):
    return product_service.get_products_by_range(init_num, end_num)


@app.get("/product/id/{uuid}")
def get_product_by_uuid(uuid: str):
    return product_service.get_product_by_uuid(uuid)


@app.get("/product/name/{product_name}")
def get_product_by_name(product_name: str):
    return product_service.get_products_by_name(product_name)


# 2024-05-03
@app.get("/delete/date/{date}")
def delete_products_by_date(date: str):
    return product_service.delete_products_by_date(date)
