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


@app.get("/get_categories")
def get_categories():
    """
    {"id": "cuidado_del_cabello", "name": "Cuidado del cabello"},
    {"id": "cuidado_facial_y_corporal", "name": "Cuidado facial y corporal"},
    {"id": "fitoterapia_y_parafarmacia", "name": "Fitoterapia y parafarmacia"},
    {"id": "limpieza_y_hogar", "name": "Limpieza y hogar"},
    {"id": "maquillaje", "name": "Maquillaje"},
    """
    return {
        "categories": [
            {"id": "aceite_especias_y_salsas", "name": "Aceite, especias y salsas"},
            {"id": "agua_y_refrescos", "name": "Agua y refrescos"},
            {"id": "aperitivos", "name": "Aperitivos"},
            {"id": "arroz_legumbres_y_pasta", "name": "Arroz, legumbres y pasta"},
            {
                "id": "azucar_caramelos_y_chocolate",
                "name": "Azúcar, caramelos y chocolate",
            },
            {"id": "bebe", "name": "Bebé"},
            {"id": "bodega", "name": "Bodega"},
            {"id": "cacao_cafe_e_infusiones", "name": "Cacao, café e infusiones"},
            {"id": "carne", "name": "Carne"},
            {"id": "cereales_y_galletas", "name": "Cereales y galletas"},
            {"id": "charcuteria_y_quesos", "name": "Charcutería y quesos"},
            {"id": "congelados", "name": "Congelados"},
            {"id": "conservas_caldos_y_cremas", "name": "Conservas, caldos y cremas"},
            {"id": "frutas", "name": "Frutas"},
            {
                "id": "huevos_leche_batidos_y_mantequilla",
                "name": "Huevos, leche, batidos y mantequilla",
            },
            {"id": "marisco_y_pescado", "name": "Marisco y pescado"},
            {"id": "mascotas", "name": "Mascotas"},
            {"id": "panaderia_y_pasteleria", "name": "Panadería y pastelería"},
            {"id": "pizzas_y_platos_preparados", "name": "Pizzas y platos preparados"},
            {"id": "postres_y_yogures", "name": "Postres y yogures"},
            {"id": "verduras", "name": "Verduras"},
            {"id": "zumos", "name": "Zumos"},
        ]
    }


@app.get("/get_all_products")
def get_all_products():
    response = requests.get("http://api_market_02:8082/get_all_products")
    message = "Desde Wewiza: "
    print(message)
    print(response.json())

    return {message: response.json()}


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
