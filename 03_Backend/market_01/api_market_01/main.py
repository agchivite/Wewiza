from fastapi import FastAPI
from api_market_01.src.services.product_service import ProductService
from api_market_01.src.database.database_manager import DatabaseManager
from api_market_01.src.repositories.product_repository import ProductRepository
import requests
import json

app = FastAPI()

# TODO: Swap in configuration if we are in docker
CONNECTION_MONGO = "mongodb://root:root@mongo_market_01:27017"
DATABASE_NAME = "donamerca"
COLLECTION_NAME = "products"

# Fachade
database_manager = DatabaseManager(CONNECTION_MONGO, DATABASE_NAME)
product_repository = ProductRepository(database_manager, COLLECTION_NAME)
product_service = ProductService(product_repository)


@app.get("/get_all_products")
def get_all_products():
    return product_service.get_all_products()


@app.get("/saludo")
def enviar_saludo():
    return {"mensaje": "¡Hola! Bienvenido desde el Contenedor Market-01"}


@app.get("/test")
def test():
    response = requests.get("http://api_wewiza:8080/saludo")
    message = "Desde Market 1: "
    print(message)
    print(response.json())

    return {message: response.json()}
