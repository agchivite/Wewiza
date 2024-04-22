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
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(10).svg?token=A3BM3IHZ7DWLQX2M5MBKSLDGE2ZGS",
            },
            {
                "id": "agua_y_refrescos",
                "name": "Agua y refrescos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(11).svg?token=A3BM3IE76LHDANBBLKZNYBTGE2ZHK",
            },
            {
                "id": "aperitivos",
                "name": "Aperitivos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(12).svg?token=A3BM3ICQIZ5TV32YH7YTKYDGE2ZHW",
            },
            {
                "id": "arroz_legumbres_y_pasta",
                "name": "Arroz, legumbres y pasta",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(13).svg?token=A3BM3IEDGBL43T4GG7TOAPDGE2ZIG",
            },
            {
                "id": "azucar_caramelos_y_chocolate",
                "name": "Azúcar, caramelos y chocolate",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(14).svg?token=A3BM3IG26CTA7WEFIJEN3S3GE2ZIW",
            },
            {
                "id": "bebe",
                "name": "Bebé",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(15).svg?token=A3BM3IAFOZDLGJ26L5SM6Z3GE2ZJE",
            },
            {
                "id": "bodega",
                "name": "Bodega",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(16).svg?token=A3BM3IFF6T527ZTPHX65VI3GE2ZJY",
            },
            {
                "id": "cacao_cafe_e_infusiones",
                "name": "Cacao, café e infusiones",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(17).svg?token=A3BM3IDP3ZLNUCJ2ORJYTK3GE2ZKU",
            },
            {
                "id": "carne",
                "name": "Carne",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(18).svg?token=A3BM3IFXCU4574KNPB5S5QDGE2ZLE",
            },
            {
                "id": "cereales_y_galletas",
                "name": "Cereales y galletas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(19).svg?token=A3BM3ICCUO25GCDXDBED4I3GE2ZLQ",
            },
            {
                "id": "charcuteria_y_quesos",
                "name": "Charcutería y quesos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(2).svg?token=A3BM3IEPADTSL3HU2FNDBRLGE2ZMC",
            },
            {
                "id": "congelados",
                "name": "Congelados",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(20).svg?token=A3BM3IEN7F6GOLS7E5PIXILGE2ZMS",
            },
            {
                "id": "conservas_caldos_y_cremas",
                "name": "Conservas, caldos y cremas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(21).svg?token=A3BM3ICSU3GZ63Y3QYL5A3TGE2ZNC",
            },
            {
                "id": "frutas",
                "name": "Frutas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(22).svg?token=A3BM3IFPA6IG4OGDCQL25T3GE2ZNQ",
            },
            {
                "id": "huevos_leche_batidos_y_mantequilla",
                "name": "Huevos, leche, batidos y mantequilla",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(3).svg?token=A3BM3IAZAXIIEO3H7ZRGXN3GE2ZN6",
            },
            {
                "id": "marisco_y_pescado",
                "name": "Marisco y pescado",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(4).svg?token=A3BM3IAZVTOWAEAZ4K2YER3GE2ZOU",
            },
            {
                "id": "mascotas",
                "name": "Mascotas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(5).svg?token=A3BM3IA3EI2IBMRUPUBCG7LGE2ZPG",
            },
            {
                "id": "panaderia_y_pasteleria",
                "name": "Panadería y pastelería",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(6).svg?token=A3BM3IEL2Y6XH7K4DV3LCULGE2ZP4",
            },
            {
                "id": "pizzas_y_platos_preparados",
                "name": "Pizzas y platos preparados",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(7).svg?token=A3BM3IEA3B566MD7I6OOPYLGE2ZQM",
            },
            {
                "id": "postres_y_yogures",
                "name": "Postres y yogures",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(8).svg?token=A3BM3IF2LLGK5425YBCFRO3GE2ZQ4",
            },
            {
                "id": "verduras",
                "name": "Verduras",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com-copia(9).svg?token=A3BM3IB2EBRVH3HOUWKOV3TGE2ZRO",
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
