from fastapi import FastAPI, Request
from api_market_03.src.services.product_service import ProductService
from api_market_03.src.database.database_manager import DatabaseManager
from api_market_03.src.repositories.product_repository import ProductRepository
import requests
import json

app = FastAPI()

# TODO: Swap in configuration if we are in docker
CONNECTION_MONGO = "mongodb://root:root@mongo_market_03:27017"
DATABASE_NAME = "carrefour"
COLLECTION_NAME = "products"

# TODO: Dependency Injection with IoC
# Fachade
database_manager = DatabaseManager(CONNECTION_MONGO, DATABASE_NAME)
product_repository = ProductRepository(database_manager, COLLECTION_NAME)
product_service = ProductService(product_repository)


@app.get("/update_all_date")
def update_all_date():
    # Only update product with "date_created" in "2024-05-01 00:14:43"
    product_service.update_all_date()
    return {"result": "All products updated"}


@app.get("/products/past/profit")
def get_products_with_good_profit():
    # Has the product data with the ["profit_percentage"] and ["profit"] key associate
    top_profit_products_list = product_service.get_products_with_good_profit()
    sorted_products = sorted(
        top_profit_products_list, key=lambda x: x["profit_percentage"], reverse=True
    )
    for product in sorted_products:
        if product["profit_percentage"] <= 0:
            sorted_products.remove(product)

    return sorted_products[:10]


@app.get("/products")
def get_all_products():
    return product_service.get_all_products()


@app.get("/size")
def get_size():
    return product_service.get_size()


@app.get("/products/market/{market_name}")
def get_all_products_by_market(market_name: str):
    return product_service.get_all_products_by_market(market_name)


@app.get("/products/range/{init_num}/{end_num}")
def get_products_by_range(init_num: int, end_num: int):
    return product_service.get_products_by_range(init_num, end_num)


@app.post("/insert_new_scrapped_product")
async def insert_new_scrapped_product(request: Request):
    data = await request.json()
    result = product_service.create_product_to_mongo_recieving_json(data)
    return {"result": result}


@app.get("/products/category/id/{category_id}")
def get_products_by_category(category_id: str):
    return product_service.get_all_products_by_category_id(category_id)


@app.get("/product/id/{uuid}")
def get_product_by_uuid(uuid: str):
    return product_service.get_product_by_uuid(uuid)


@app.get("/product/name/{product_name}")
def get_product_by_name(product_name: str):
    return product_service.get_products_by_name(product_name)


@app.get("/update/measure_carrefour")
def update_price_by_standard_measure():
    return product_service.update_price_by_standard_measure()