from fastapi import FastAPI
from api_wewiza.src.database.database_manager import DatabaseManager
from api_wewiza.src.repositories.product_likes_repository import ProductLikesRepository
from api_wewiza.src.services.product_likes_service import ProductLikesService
import requests
import datetime

app = FastAPI()

# TODO: Inyect with IoC
CONNECTION_MONGO = "mongodb://root:root@mongo_wewiza:27017"
DATABASE_NAME = "wewiza"
COLLECTION_NAME = "products"

# Fachade
database_manager = DatabaseManager(CONNECTION_MONGO, DATABASE_NAME)
product_repository = ProductLikesRepository(database_manager, COLLECTION_NAME)
product_service = ProductLikesService(product_repository)

# https://127.0.0.1 -> To call API


@app.get("/")
def read_root():
    # Set all the endpoints
    return {
        "endpoints": [
            {
                "endpoint": "/",
                "description": "Root endpoint",
            },
            {
                "endpoint": "/categories",
                "description": "Get all categories",
            },
            {
                "endpoint": "/products",
                "description": "Get all products",
            },
            {
                "endpoint": "/products/{market_name}",
                "description": "Get all products by market",
            },
            {
                "endpoint": "/products/{market_name}/{init_num}/{end_num}",
                "description": "Get all products by market and range, start index = 0",
            },
            {
                "endpoint": "/size/{market_name}",
                "description": "Get the size of products by market",
            },
            {
                "endpoint": "/products/{category_id}",
                "description": "Get all products by category",
            },
            {
                "endpoint": "/product/{product_id}/{market_name}",
                "description": "Get a product by id and market",
            },
            {
                "endpoint": "/like/{product_id}/{email_user}",
                "description": "Like a product only one time per user",
            },
            {
                "endpoint": "/unlike/{product_id}/{email_user}",
                "description": "Unlike a product only one time per user",
            },
            {
                "endpoint": "/start_likes",
                "description": "Start the database with likes",
                "warning": "This will reset all the likes, only is used when scrapping monthly",
            },
        ]
    }


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
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "agua_y_refrescos",
                "name": "Agua y refrescos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "aperitivos",
                "name": "Aperitivos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "arroz_legumbres_y_pasta",
                "name": "Arroz, legumbres y pasta",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "azucar_caramelos_y_chocolate",
                "name": "Azúcar, caramelos y chocolate",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "bebe",
                "name": "Bebé",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "bodega",
                "name": "Bodega",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "cacao_cafe_e_infusiones",
                "name": "Cacao, café e infusiones",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "carne",
                "name": "Carne",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "cereales_y_galletas",
                "name": "Cereales y galletas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "charcuteria_y_quesos",
                "name": "Charcutería y quesos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "congelados",
                "name": "Congelados",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "conservas_caldos_y_cremas",
                "name": "Conservas, caldos y cremas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "frutas",
                "name": "Frutas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "huevos_leche_batidos_y_mantequilla",
                "name": "Huevos, leche, batidos y mantequilla",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "marisco_y_pescado",
                "name": "Marisco y pescado",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "mascotas",
                "name": "Mascotas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "panaderia_y_pasteleria",
                "name": "Panadería y pastelería",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "pizzas_y_platos_preparados",
                "name": "Pizzas y platos preparados",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "postres_y_yogures",
                "name": "Postres y yogures",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "verduras",
                "name": "Verduras",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
            },
            {
                "id": "zumos",
                "name": "Zumos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/67b5946b83616724659dcf55319ae672807bec7c/category-svgrepo-com.svg",
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

    # Map objetcs with LIKES data
    map_list_market_01 = product_service.map_products_json_list(
        response_products_market_01_json_list
    )
    map_list_market_02 = product_service.map_products_json_list(
        response_products_market_02_json_list
    )

    return {
        "mercadona": filter_current_month_elements(map_list_market_01),
        "ahorramas": filter_current_month_elements(map_list_market_02),
        "carrefour": response_products_market_03_json_list,
    }


@app.get("/products/{market_name}")
def get_all_products_by_market(market_name: str):
    if market_name.lower().strip() == "mercadona":
        response_products_market_01_json_list = requests.get(
            f"http://api_market_01:8081/products/Mercadona"
        ).json()
        map_list_market_01 = product_service.map_products_json_list(
            response_products_market_01_json_list
        )
        return filter_current_month_elements(map_list_market_01)

    if market_name.lower().strip() == "ahorramas":
        response_products_market_02_json_list = requests.get(
            f"http://api_market_02:8082/products/Ahorramas"
        ).json()
        map_list_market_02 = product_service.map_products_json_list(
            response_products_market_02_json_list
        )
        return filter_current_month_elements(map_list_market_02)

    # TODO: add market 03 = carrefour


def filter_current_month_elements(elements):
    current_date_time = datetime.datetime.now()
    current_month = current_date_time.month  # Obtener el mes actual

    filtered_elements = [
        element
        for element in elements
        if parse_date(element["date_created"]).month == current_month
    ]

    return filtered_elements


def parse_date(date_str):
    return datetime.datetime.strptime(date_str, "%Y-%m-%d %H:%M:%S")


# Is for details of a product an his chart with historical
@app.get("/product/{product_id}/{market_name}")
def get_product(product_id: str, market_name: str):

    if market_name.lower().strip() == "mercadona":
        response_product_market_01_json = requests.get(
            "http://api_market_01:8081/product/" + product_id
        ).json()
        mapped_product = product_service.map_product_json(
            response_product_market_01_json
        )
        response_list_products_market_01_json_list = requests.get(
            "http://api_market_01:8081/product/name/" + mapped_product["name"]
        ).json()

        product_uuid = mapped_product["uuid"]
        response_list_products_market_01_json_list = [
            product
            for product in response_list_products_market_01_json_list
            if product.get("uuid") != product_uuid
        ]

        response_list_products_market_01_json_list.append(mapped_product)
        return response_list_products_market_01_json_list

    if market_name.lower().strip() == "ahorramas":
        response_product_market_02_json = requests.get(
            "http://api_market_02:8082/product/" + product_id
        ).json()
        mapped_product = product_service.map_product_json(
            response_product_market_02_json
        )
        response_list_products_market_02_json_list = requests.get(
            "http://api_market_02:8082/product/name/" + mapped_product["name"]
        ).json()

        product_uuid = mapped_product["uuid"]
        response_list_products_market_02_json_list = [
            product
            for product in response_list_products_market_02_json_list
            if product.get("uuid") != product_uuid
        ]

        response_list_products_market_02_json_list.append(mapped_product)

        return response_list_products_market_02_json_list

    # TODO: add market 03 = carrefour


@app.get("/products/{market_name}/{init_num}/{end_num}")
def get_products_by_range(market_name: str, init_num: int, end_num: int):
    if market_name.lower().strip() == "mercadona":
        response_products_market_01_json_list = requests.get(
            "http://api_market_01:8081/products/" + str(init_num) + "/" + str(end_num)
        ).json()
        return product_service.map_products_json_list(
            response_products_market_01_json_list
        )

    if market_name.lower().strip() == "ahorramas":
        response_products_market_02_json_list = requests.get(
            "http://api_market_02:8082/products/" + str(init_num) + "/" + str(end_num)
        ).json()
        return product_service.map_products_json_list(
            response_products_market_02_json_list
        )

    # TODO: response_products_market_03_json_list
    return list()


@app.get("/size/{market_name}")
def get_products_by_category(market_name: str):
    if market_name.lower().strip() == "mercadona":
        response_products_market_01_json_list = requests.get(
            "http://api_market_01:8081/size"
        ).json()
        return response_products_market_01_json_list

    if market_name.lower().strip() == "ahorramas":
        response_products_market_02_json_list = requests.get(
            "http://api_market_02:8082/size"
        ).json()
        return response_products_market_02_json_list

    # TODO: last market
    return 0


@app.get("/products/{category_id}")
def get_products_by_category(category_id: str):
    response_products_market_01_json_list = requests.get(
        "http://api_market_01:8081/products/" + category_id
    ).json()

    response_products_market_02_json_list = requests.get(
        "http://api_market_02:8082/products/" + category_id
    ).json()

    # TODO: add market 03 = carrefour
    response_products_market_03_json_list = list()

    # Map objetcs with LIKES data
    map_list_market_01 = product_service.map_products_json_list(
        response_products_market_01_json_list
    )
    map_list_market_02 = product_service.map_products_json_list(
        response_products_market_02_json_list
    )

    # TODO: Filter products that are in the same month that today

    return {
        "mercadona": map_list_market_01,
        "ahorramas": map_list_market_02,
        "carrefour": response_products_market_03_json_list,
    }


@app.get("/like/{product_id}/{email_user}")
def like_product(product_id: str, email_user: str):
    messsage = product_service.like_product(product_id, email_user)
    return {"message": str(messsage)}


@app.get("/unlike/{product_id}/{email_user}")
def unlike_product(product_id: str, email_user: str):
    messsage = product_service.unlike_product(product_id, email_user)
    return {"message": str(messsage)}


# TODO: endpoint to check if a user has liked a product and unliked


@app.get("/start_likes")
def start_likes_database():
    response_products_market_01_json_list = requests.get(
        "http://api_market_01:8081/products"
    ).json()

    response_products_market_02_json_list = requests.get(
        "http://api_market_02:8082/products"
    ).json()

    # TODO: market_03

    product_service.delete_all_products_by_actual_month()

    product_service.insert_products_json_list(response_products_market_01_json_list)
    product_service.insert_products_json_list(response_products_market_02_json_list)

    return {"message": "Database likes updated"}
