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

# https://127.0.0.1 -> To call API


@app.get("/categories")
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
            {
                "id": "aceite_especias_y_salsas",
                "name": "Aceite, especias y salsas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(10).svg?token=A3BM3IAC3RMARDHYZAZAZULGE2XKQ",
            },
            {
                "id": "agua_y_refrescos",
                "name": "Agua y refrescos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(11).svg?token=A3BM3ID6NH3TEBASYRUFFZDGE2XNW",
            },
            {
                "id": "aperitivos",
                "name": "Aperitivos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(12).svg?token=A3BM3IEKVV2R3ADHTQXK4S3GE2XPK",
            },
            {
                "id": "arroz_legumbres_y_pasta",
                "name": "Arroz, legumbres y pasta",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(13).svg?token=A3BM3IBAYYL6HPOOCVFDTBTGE2XUE",
            },
            {
                "id": "azucar_caramelos_y_chocolate",
                "name": "Azúcar, caramelos y chocolate",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(14).svg?token=A3BM3IA7FUEKHSTFUA6RMY3GE2XUU",
            },
            {
                "id": "bebe",
                "name": "Bebé",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(15).svg?token=A3BM3IAXDJWZZRQ6Y3T5QADGE2XVC",
            },
            {
                "id": "bodega",
                "name": "Bodega",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(16).svg?token=A3BM3IBC7MV25MERO6GOFXDGE2XVW",
            },
            {
                "id": "cacao_cafe_e_infusiones",
                "name": "Cacao, café e infusiones",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(17).svg?token=A3BM3IE3PPUAZDDP3BH6W7TGE2XWE",
            },
            {
                "id": "carne",
                "name": "Carne",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(18).svg?token=A3BM3IHNHY3NFBACD7HFYJDGE2XWU",
            },
            {
                "id": "cereales_y_galletas",
                "name": "Cereales y galletas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(19).svg?token=A3BM3IFVXXSPG3HG4M7JST3GE2XXC",
            },
            {
                "id": "charcuteria_y_quesos",
                "name": "Charcutería y quesos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(2).svg?token=A3BM3IBVQC7Z5BRKHCSNIILGE2XXQ",
            },
            {
                "id": "congelados",
                "name": "Congelados",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(20).svg?token=A3BM3IAURP55NUZ6TRCMWJTGE2XYA",
            },
            {
                "id": "conservas_caldos_y_cremas",
                "name": "Conservas, caldos y cremas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(21).svg?token=A3BM3IERT67UMPV6ZFZSC3LGE2XYO",
            },
            {
                "id": "frutas",
                "name": "Frutas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(3).svg?token=A3BM3IDU4TBGEQ34GWFDQW3GE2XZC",
            },
            {
                "id": "huevos_leche_batidos_y_mantequilla",
                "name": "Huevos, leche, batidos y mantequilla",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(4).svg?token=A3BM3IEFMRIQBURVLXACT5TGE2XZS",
            },
            {
                "id": "marisco_y_pescado",
                "name": "Marisco y pescado",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(5).svg?token=A3BM3IE2PJXW6VX46IFGST3GE2XZ6",
            },
            {
                "id": "mascotas",
                "name": "Mascotas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(6).svg?token=A3BM3IAESKVJJBMDSR6Q56LGE2X2M",
            },
            {
                "id": "panaderia_y_pasteleria",
                "name": "Panadería y pastelería",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(7).svg?token=A3BM3ICOOVUBRZXWE2VEU5TGE2X24",
            },
            {
                "id": "pizzas_y_platos_preparados",
                "name": "Pizzas y platos preparados",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(8).svg?token=A3BM3ICQ6X4X7CWQD33555LGE2X3K",
            },
            {
                "id": "postres_y_yogures",
                "name": "Postres y yogures",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia%20(9).svg?token=A3BM3IHGCSDTE3UJCWKX7QLGE2X32",
            },
            {
                "id": "verduras",
                "name": "Verduras",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com%20-%20copia.svg?token=A3BM3IH3TPO5AXDVHNBEES3GE2X4G",
            },
            {
                "id": "zumos",
                "name": "Zumos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/a861f399f2f4a6c9f3bb6f79fe88b2fc3fd848ad/category-svgrepo-com.svg?token=A3BM3ICA3SEATE6FV24NWODGE2X4U",
            },
        ]
    }


@app.get("/products")
def get_all_products():
    response_products_market_01_json_list = requests.get(
        "http://api_market_01:8081/products"
    ).json()

    response_products_market_02_json_list = requests.get(
        "http://api_market_02:8082/products"
    ).json()

    # TODO: add market 03 = carrefour
    response_products_market_03_json_list = list()

    return {
        "mercadona": response_products_market_01_json_list,
        "ahorramas": response_products_market_02_json_list,
        "carrefour": response_products_market_03_json_list,
    }


@app.get("/update_likes_database")
def update_database():
    response_products_market_01_json_list = requests.get(
        "http://api_market_01:8081/get_all_products"
    ).json()

    product_service.insert_products_json_list(response_products_market_01_json_list)

    # TODO: market_02 and market_03

    return {"message": "Database likes updated"}
