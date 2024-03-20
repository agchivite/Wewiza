from fastapi import FastAPI
from api_wewiza.src.database.database_manager import DatabaseManager
from api_wewiza.src.repositories.product_likes_repository import ProductLikesRepository
from api_wewiza.src.services.product_likes_service import ProductLikesService
import requests

app = FastAPI()

# TODO: Inyect with IoC
CONNECTION_MONGO = "mongodb://root:root@mongo_wewiza:27017"
DATABASE_NAME = "wewiza_db"
COLLECTION_NAME = "products_likes"
# Fachade
database_manager = DatabaseManager(CONNECTION_MONGO, DATABASE_NAME)
product_repository = ProductLikesRepository(database_manager, COLLECTION_NAME)
product_service = ProductLikesService(product_repository)


@app.get("/saludo")
def enviar_saludo():
    return {"mensaje": "¡Hola! Bienvenido desde el Contenedor Wewiza"}


@app.get("/test")
def enviar_saludo():
    response = requests.get("http://api_market_02:8082/saludo")
    message = "Desde Wewiza: "
    print(message)
    print(response.json())

    return {message: response.json()}


@app.get("/update_database_from_markets")
def update_database():
    response_products_market_01_json_list = requests.get(
        "http://api_market_01:8081/get_all_products"
    ).json()

    product_service.insert_products_json_list(response_products_market_01_json_list)

    # TODO: market_02 duplicate the market_01

    return {"message": "Database updated"}
