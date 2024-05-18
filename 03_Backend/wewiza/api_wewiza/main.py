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


# TODO: CLASS UTILS...
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


@app.get("/")
def read_root():
    # Set all the endpoints
    return {
        "docs": [
            {
                "endpoint": "/docs",
                "description": "To see all endpoints allowed and documentation",
            }
        ]
    }


@app.get(
    "/reaction/email/{email_user}/product/id/{product_id}",
    description="Check if a user has liked a product",
)
def get_reaction(email_user: str, product_id: str):
    reaction = product_service.get_reaction(email_user, product_id)
    return {"reaction": reaction}


@app.get("/categories", description="Get all categories")
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


@app.get(
    "/products/category/id/{category_id}",
    description="Get all products by category",
)
def get_products_by_category(category_id: str):
    response_products_market_01_json_list = requests.get(
        "http://api_market_01:8081/products/category/id/" + category_id
    ).json()

    response_products_market_02_json_list = requests.get(
        "http://api_market_02:8082/products/category/id/" + category_id
    ).json()

    response_products_market_03_json_list = requests.get(
        "http://api_market_03:8083/products/category/id/" + category_id
    ).json()

    # Map objetcs with LIKES data
    map_list_market_01 = product_service.map_products_json_list(
        response_products_market_01_json_list
    )
    map_list_market_02 = product_service.map_products_json_list(
        response_products_market_02_json_list
    )
    map_list_market_03 = product_service.map_products_json_list(
        response_products_market_03_json_list
    )

    # TODO: Filter products that are in the same month that today

    return {
        "mercadona": map_list_market_01,
        "ahorramas": map_list_market_02,
        "carrefour": map_list_market_03,
    }


@app.get(
    "/products",
    description="Get all products",
)
def get_all_products():
    response_products_market_01_json_list = requests.get(
        "http://api_market_01:8081/products"
    ).json()

    response_products_market_02_json_list = requests.get(
        "http://api_market_02:8082/products"
    ).json()

    response_products_market_03_json_list = requests.get(
        "http://api_market_03:8083/products"
    ).json()

    # Map objetcs with LIKES data
    map_list_market_01 = product_service.map_products_json_list(
        response_products_market_01_json_list
    )
    map_list_market_02 = product_service.map_products_json_list(
        response_products_market_02_json_list
    )
    map_list_market_03 = product_service.map_products_json_list(
        response_products_market_03_json_list
    )

    return {
        "mercadona": filter_current_month_elements(map_list_market_01),
        "ahorramas": filter_current_month_elements(map_list_market_02),
        "carrefour": filter_current_month_elements(map_list_market_03),
    }


@app.get(
    "/products/market/{market_name}",
    description="Get all products by market name (no case sensitive), if not found returns empty list",
)
def get_all_products_by_market(market_name: str):
    if market_name.lower().strip() == "mercadona":
        response_products_market_01_json_list = requests.get(
            f"http://api_market_01:8081/products/market/Mercadona"
        ).json()
        map_list_market_01 = product_service.map_products_json_list(
            response_products_market_01_json_list
        )
        return filter_current_month_elements(map_list_market_01)

    if market_name.lower().strip() == "ahorramas":
        response_products_market_02_json_list = requests.get(
            f"http://api_market_02:8082/products/market/Ahorramas"
        ).json()
        map_list_market_02 = product_service.map_products_json_list(
            response_products_market_02_json_list
        )
        return filter_current_month_elements(map_list_market_02)

    if market_name.lower().strip() == "carrefour":
        response_products_market_03_json_list = requests.get(
            f"http://api_market_03:8083/products/market/Carrefour"
        ).json()
        map_list_market_03 = product_service.map_products_json_list(
            response_products_market_03_json_list
        )
        return filter_current_month_elements(map_list_market_03)

    return []


@app.get(
    "/product/id/{product_id}",
    description="Product details by id, if not found returns 'name': 'Product not found'",
)
def get_product_by_id(product_id: str):
    response_product_market_01_raw_json = requests.get(
        "http://api_market_01:8081/product/id/" + product_id
    )

    if response_product_market_01_raw_json.status_code == 200:
        mapped_product = product_service.map_product_json(
            response_product_market_01_raw_json.json()
        )
        return mapped_product

    response_product_market_02_raw_json = requests.get(
        "http://api_market_02:8082/product/id/" + product_id
    )

    if response_product_market_02_raw_json.status_code == 200:
        mapped_product = product_service.map_product_json(
            response_product_market_02_raw_json.json()
        )
        return mapped_product

    response_product_market_03_raw_json = requests.get(
        "http://api_market_03:8083/product/id/" + product_id
    )

    if response_product_market_03_raw_json.status_code == 200:
        mapped_product = product_service.map_product_json(
            response_product_market_03_raw_json.json()
        )
        return mapped_product

    return {"name": "Product not found"}


@app.get(
    "/product/details/id/{product_id}/market/{market_name}",
    description="List of same product with different date_created, the latest one will be the main product, if not found returns empty list",
)
def get_product_details_by_id_and_market(product_id: str, market_name: str):

    if market_name.lower().strip() == "mercadona":
        response_product_market_01_json = requests.get(
            "http://api_market_01:8081/product/id/" + product_id
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
            "http://api_market_02:8082/product/id/" + product_id
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

    if market_name.lower().strip() == "carrefour":
        response_product_market_03_json = requests.get(
            "http://api_market_03:8083/product/id/" + product_id
        ).json()
        mapped_product = product_service.map_product_json(
            response_product_market_03_json
        )
        response_list_products_market_03_json_list = requests.get(
            "http://api_market_03:8083/product/name/" + mapped_product["name"]
        ).json()

        product_uuid = mapped_product["uuid"]
        response_list_products_market_03_json_list = [
            product
            for product in response_list_products_market_03_json_list
            if product.get("uuid") != product_uuid
        ]

        response_list_products_market_03_json_list.append(mapped_product)
        return response_list_products_market_03_json_list

    return []


@app.get(
    "/product/details/id/{product_id}",
    description="List of same product with different date_created, the latest one will be the main product, if not found returns empty list",
)
def get_product_details_by_id(product_id: str):
    # MARKET 01
    response_product_market_01_json = requests.get(
        "http://api_market_01:8081/product/id/" + product_id
    ).json()
    mapped_product = product_service.map_product_json(response_product_market_01_json)
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
    if response_list_products_market_01_json_list != []:
        return response_list_products_market_01_json_list

    # MARKET 02
    response_product_market_02_json = requests.get(
        "http://api_market_02:8082/product/id/" + product_id
    ).json()
    mapped_product = product_service.map_product_json(response_product_market_02_json)
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

    if response_list_products_market_02_json_list != []:
        return response_list_products_market_02_json_list

    # TODO: response_product_market_03_json
    # MARKET 03
    response_product_market_03_json = requests.get(
        "http://api_market_03:8083/product/id/" + product_id
    )
    mapped_product = product_service.map_product_json(response_product_market_03_json)
    response_list_products_market_03_json_list = requests.get(
        "http://api_market_03:8083/product/name/" + mapped_product["name"]
    )

    product_uuid = mapped_product["uuid"]
    response_list_products_market_03_json_list = [
        product
        for product in response_list_products_market_03_json_list
        if product.get("uuid") != product_uuid
    ]

    response_list_products_market_03_json_list.append(mapped_product)

    if response_list_products_market_03_json_list != []:
        return response_list_products_market_03_json_list

    return []


@app.get(
    "/products/market/{market_name}/range/{init_num}/{end_num}",
    description="Get all products by market and range (start index = 0), if not found returns empty list",
)
def get_products_by_range(market_name: str, init_num: int, end_num: int):
    if market_name.lower().strip() == "mercadona":
        response_products_market_01_json_list = requests.get(
            "http://api_market_01:8081/products/range/"
            + str(init_num)
            + "/"
            + str(end_num)
        ).json()
        return product_service.map_products_json_list(
            response_products_market_01_json_list
        )

    if market_name.lower().strip() == "ahorramas":
        response_products_market_02_json_list = requests.get(
            "http://api_market_02:8082/products/range/"
            + str(init_num)
            + "/"
            + str(end_num)
        ).json()
        return product_service.map_products_json_list(
            response_products_market_02_json_list
        )

    if market_name.lower().strip() == "carrefour":
        response_products_market_03_json_list = requests.get(
            "http://api_market_03:8083/products/range/"
            + str(init_num)
            + "/"
            + str(end_num)
        ).json()
        return product_service.map_products_json_list(
            response_products_market_03_json_list
        )

    return []


@app.get(
    "/size/market/{market_name}",
    description="Get the size of products by market, return number",
)
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

    if market_name.lower().strip() == "carrefour":
        response_products_market_03_json_list = requests.get(
            "http://api_market_03:8083/size"
        ).json()
        return response_products_market_03_json_list

    return 0


@app.get(
    "/like/{product_id}/email/{email_user}",
    description="Like a product only one time per user, return true if liked or false if was liked before",
)
def like_product(product_id: str, email_user: str):
    boolean_result = product_service.like_product(product_id, email_user)
    return {"result": boolean_result}


@app.get(
    "/unlike/{product_id}/email/{email_user}",
    description="Unlike a product only one time per user, true if unliked or false if was unliked before",
)
def unlike_product(product_id: str, email_user: str):
    boolean_result = product_service.unlike_product(product_id, email_user)
    return {"result": boolean_result}


# TODO: endpoint to check if a user has liked a product and unliked


@app.get(
    "/start_likes",
    description="Start the database with likes in the same month we are, this will reset all the likes, only is used when scrapping monthly",
)
def start_likes_database():
    response_products_market_01_json_list = requests.get(
        "http://api_market_01:8081/products"
    ).json()

    response_products_market_02_json_list = requests.get(
        "http://api_market_02:8082/products"
    ).json()

    response_products_market_03_json_list = requests.get(
        "http://api_market_03:8083/products"
    ).json()

    # Directly delete all products in the current month to reset
    product_service.delete_all_products_by_actual_month()

    product_service.insert_products_json_list(response_products_market_01_json_list)
    product_service.insert_products_json_list(response_products_market_02_json_list)
    product_service.insert_products_json_list(response_products_market_03_json_list)

    return {"message": "Database likes updated"}
