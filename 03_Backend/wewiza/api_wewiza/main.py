from typing import List
from fastapi import FastAPI, BackgroundTasks, Query
from api_wewiza.src.database.database_manager import DatabaseManager
from api_wewiza.src.repositories.product_likes_repository import ProductLikesRepository
from api_wewiza.src.services.product_likes_service import ProductLikesService
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.cron import CronTrigger
import requests
import datetime
import requests
import time


def check_service_availability(url):
    try:
        response = requests.get(url, timeout=5)
        return response.status_code == 200
    except requests.RequestException:
        return False


def main():
    while True:
        if all(
            check_service_availability(url)
            for url in [
                "http://api_market_01:8081/check",
                "http://api_market_02:8082/check",
                "http://api_market_03:8083/check",
            ]
        ):
            print("All APIs Market are available.")
            break
        else:
            print("At least one API Market is not available. Retrying in 5 seconds...")
            time.sleep(5)


main()

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

# GLOBAL STATEMETS TO CALCULATE TOPICS
# 1. Server start
# 2. like/unlike product
# 3. Start new month with start likes
TOP_LIKES_AVERAGE = 0
CATEGORIES_TOP = []
PRODUCTS_TOP = []


##################### TODO: CLASS UTILS... #####################
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


def get_all_categories():
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
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/aceite_especias_y_salsas.svg",
            },
            {
                "id": "agua_y_refrescos",
                "name": "Agua y refrescos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/agua_y_refrescos.svg",
            },
            {
                "id": "aperitivos",
                "name": "Aperitivos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/aperitivos.svg",
            },
            {
                "id": "arroz_legumbres_y_pasta",
                "name": "Arroz, legumbres y pasta",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/arroz_legumbres_y_pasta.svg",
            },
            {
                "id": "azucar_caramelos_y_chocolate",
                "name": "Azúcar, caramelos y chocolate",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/azucar_caramelos_y_chocolate.svg",
            },
            {
                "id": "bebe",
                "name": "Bebé",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/bebe.svg",
            },
            {
                "id": "bodega",
                "name": "Bodega",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/bodega.svg",
            },
            {
                "id": "cacao_cafe_e_infusiones",
                "name": "Cacao, café e infusiones",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/cacao_cafe_e_infusiones.svg",
            },
            {
                "id": "carne",
                "name": "Carne",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/carne.svg",
            },
            {
                "id": "cereales_y_galletas",
                "name": "Cereales y galletas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/cereales_y_galletas.svg",
            },
            {
                "id": "charcuteria_y_quesos",
                "name": "Charcutería y quesos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/charcuteria_y_quesos.svg",
            },
            {
                "id": "congelados",
                "name": "Congelados",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/congelados.svg",
            },
            {
                "id": "conservas_caldos_y_cremas",
                "name": "Conservas, caldos y cremas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/conservas_caldos_y_cremas.svg",
            },
            {
                "id": "frutas",
                "name": "Frutas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/frutas.svg",
            },
            {
                "id": "huevos_leche_batidos_y_mantequilla",
                "name": "Huevos, leche, batidos y mantequilla",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/huevos_leche_batidos_y_mantequilla.svg",
            },
            {
                "id": "marisco_y_pescado",
                "name": "Marisco y pescado",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/marisco_y_pescado.svg",
            },
            {
                "id": "mascotas",
                "name": "Mascotas",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/mascotas.svg",
            },
            {
                "id": "panaderia_y_pasteleria",
                "name": "Panadería y pastelería",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/panaderia_y_pasteleria.svg",
            },
            {
                "id": "pizzas_y_platos_preparados",
                "name": "Pizzas y platos preparados",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/pizzas_y_platos_preparados.svg",
            },
            {
                "id": "postres_y_yogures",
                "name": "Postres y yogures",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/postres_y_yogures.svg",
            },
            {
                "id": "verduras",
                "name": "Verduras",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/verduras.svg",
            },
            {
                "id": "zumos",
                "name": "Zumos",
                "icon": "https://raw.githubusercontent.com/JiaChengZhang14/Wewiza-Icons/44de8e7a305c634e61a81e43bd3c5bff3efffe0f/zumos.svg",
            },
        ]
    }


def find_actual_product_by_uuid_past(uuid_past_product):
    response_1 = requests.get(
        "http://api_market_01:8081/find/actual/id/" + uuid_past_product
    ).json()
    if response_1["success"] == True:
        return response_1["uuid"]

    response_2 = requests.get(
        "http://api_market_02:8082/find/actual/id/" + uuid_past_product
    ).json()
    if response_2["success"] == True:
        return response_2["uuid"]

    response_3 = requests.get(
        "http://api_market_03:8083/find/actual/id/" + uuid_past_product
    ).json()
    if response_3["success"] == True:
        return response_3["uuid"]

    print("FINAL PRODUCT: " + uuid_past_product)
    return uuid_past_product


#####################----------------#####################


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
    "/categories/top",
    description="Get maximum 6 top categories, it depends on top products",
)
def get_top_categories():
    all_categories = get_all_categories()
    top_categories = []
    for category in all_categories["categories"]:
        if category["id"] in CATEGORIES_TOP:
            top_categories.append(category)

    return top_categories[:6]


@app.get(
    "/products/top",
    description="Get top products with benefits comparing the past or good likes, if the key ['profit'] and ['profit_percentage'] are 0 it means that the product is TOP because it has A LOT LIKES",
)
def get_top_products():
    return PRODUCTS_TOP


@app.get("/categories", description="Get all categories")
def get_categories():
    return get_all_categories()


@app.get(
    "/products/category/id/{category_id}",
    description="Get all products by category in the current month",
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

    return {
        "mercadona": filter_current_month_elements(map_list_market_01),
        "ahorramas": filter_current_month_elements(map_list_market_02),
        "carrefour": filter_current_month_elements(map_list_market_03),
    }


@app.get(
    "/products/market/{market_name}",
    description="Get all products by market name (no case sensitive) in the current month, if not found returns empty list",
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
    "/product/id/{uuid}",
    description="Product details by id, if not found returns 'name': 'Product not found'",
)
def get_product_by_id(uuid: str):
    product_id = find_actual_product_by_uuid_past(uuid)

    if product_id is None:
        return {"name": "Product not found"}

    response_product_market_01_raw_json = requests.get(
        "http://api_market_01:8081/product/id/" + product_id
    )

    if response_product_market_01_raw_json.json() is not None:
        mapped_product = product_service.map_product_json(
            response_product_market_01_raw_json.json()
        )
        return mapped_product

    response_product_market_02_raw_json = requests.get(
        "http://api_market_02:8082/product/id/" + product_id
    )

    if response_product_market_02_raw_json.json() is not None:
        mapped_product = product_service.map_product_json(
            response_product_market_02_raw_json.json()
        )
        return mapped_product

    response_product_market_03_raw_json = requests.get(
        "http://api_market_03:8083/product/id/" + product_id
    )

    if response_product_market_03_raw_json.json() is not None:
        mapped_product = product_service.map_product_json(
            response_product_market_03_raw_json.json()
        )
        return mapped_product

    return {"name": "Product not found"}


@app.get(
    "/product/details/id/{uuid}",
    description="List of same product with different date_created, the latest one will be the main product, if not found returns empty list",
)
def get_product_details_by_id(uuid: str):
    product_id = find_actual_product_by_uuid_past(uuid)

    if product_id is None:
        return []

    # MARKET 01
    response_product_market_01_json = requests.get(
        "http://api_market_01:8081/product/id/" + product_id
    ).json()
    mapped_product = product_service.map_product_json(response_product_market_01_json)

    if isinstance(mapped_product, requests.Response):
        mapped_product = mapped_product.json()

    if isinstance(mapped_product, dict) and mapped_product is not None:
        response_list_products_market_01_json_list = requests.get(
            "http://api_market_01:8081/product/name/" + mapped_product["name"]
        ).json()

        product_uuid = mapped_product["uuid"]
        filtered_products = []
        for product in response_list_products_market_01_json_list:
            if product is not None and product["uuid"] != product_uuid:
                filtered_products.append(product)
        response_list_products_market_01_json_list = filtered_products

        response_list_products_market_01_json_list.append(mapped_product)
        if response_list_products_market_01_json_list != []:
            return response_list_products_market_01_json_list

    # MARKET 02
    response_product_market_02_json = requests.get(
        "http://api_market_02:8082/product/id/" + product_id
    ).json()
    mapped_product = product_service.map_product_json(response_product_market_02_json)

    if isinstance(mapped_product, requests.Response):
        mapped_product = mapped_product.json()

    if isinstance(mapped_product, dict) and mapped_product is not None:
        response_list_products_market_02_json_list = requests.get(
            "http://api_market_02:8082/product/name/" + mapped_product["name"]
        ).json()

        product_uuid = mapped_product["uuid"]
        filtered_products = []
        for product in response_list_products_market_02_json_list:
            if product is not None and product["uuid"] != product_uuid:
                filtered_products.append(product)
        response_list_products_market_02_json_list = filtered_products

        response_list_products_market_02_json_list.append(mapped_product)

        if response_list_products_market_02_json_list != []:
            return response_list_products_market_02_json_list

    # MARKET 03
    response_product_market_03_json = requests.get(
        "http://api_market_03:8083/product/id/" + product_id
    )
    mapped_product = product_service.map_product_json(response_product_market_03_json)

    if isinstance(mapped_product, requests.Response):
        mapped_product = mapped_product.json()

    if isinstance(mapped_product, dict) and mapped_product is not None:
        response_list_products_market_03_json_list = requests.get(
            "http://api_market_03:8083/product/name/" + mapped_product["name"]
        ).json()

        product_uuid = mapped_product["uuid"]
        filtered_products = []
        for product in response_list_products_market_03_json_list:
            if product is not None and product["uuid"] != product_uuid:
                filtered_products.append(product)
        response_list_products_market_03_json_list = filtered_products

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
        map_list_market_01 = product_service.map_products_json_list(
            response_products_market_01_json_list
        )
        return filter_current_month_elements(map_list_market_01)

    if market_name.lower().strip() == "ahorramas":
        response_products_market_02_json_list = requests.get(
            "http://api_market_02:8082/products/range/"
            + str(init_num)
            + "/"
            + str(end_num)
        ).json()
        map_list_market_02 = product_service.map_products_json_list(
            response_products_market_02_json_list
        )
        return filter_current_month_elements(map_list_market_02)

    if market_name.lower().strip() == "carrefour":
        response_products_market_03_json_list = requests.get(
            "http://api_market_03:8083/products/range/"
            + str(init_num)
            + "/"
            + str(end_num)
        ).json()
        map_list_market_03 = product_service.map_products_json_list(
            response_products_market_03_json_list
        )
        return filter_current_month_elements(map_list_market_03)

    return []


@app.get(
    "/size/market/{market_name}",
    description="Get the size of products by market, return number",
)
def get_size_by_market(market_name: str):
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


##################### TODO: WHHAT CLASS MUST IMPLEMENT THIS... #####################
# THIS MUST BE HERE BECAUSE WE NEED TO LOAD FUCNTIONS EARLIER THAT like / unlike and start_likes endpoints
def calculate_like_average():
    return product_service.calculate_like_average()


def calculate_top_products():
    # We get the list with uuid´s
    top_likes_products_uuid_list = product_service.get_top_products(TOP_LIKES_AVERAGE)
    top_likes_product = []

    for product_uuid in top_likes_products_uuid_list:
        respond_market_01 = requests.get(
            "http://api_market_01:8081/product/id/" + product_uuid
        )
        if respond_market_01.status_code == 200:
            top_likes_product.append(respond_market_01.json())

        respond_market_02 = requests.get(
            "http://api_market_02:8082/product/id/" + product_uuid
        )
        if respond_market_02.status_code == 200:
            top_likes_product.append(respond_market_02.json())

        respond_market_03 = requests.get(
            "http://api_market_03:8083/product/id/" + product_uuid
        )
        if respond_market_03.status_code == 200:
            top_likes_product.append(respond_market_03.json())

    # Delete None items
    top_likes_product = [product for product in top_likes_product if product]

    # Adding 0 to know this product is TOP because it has A LOT LIKES
    for product in top_likes_product:
        product["profit"] = 0
        product["profit_percentage"] = 0

    # This list has the product data with the profit associate
    response_products_profit_market_01_json_list = requests.get(
        "http://api_market_01:8081/products/past/profit"
    ).json()

    response_products_profit_market_02_json_list = requests.get(
        "http://api_market_02:8082/products/past/profit"
    ).json()

    response_products_profit_market_03_json_list = requests.get(
        "http://api_market_03:8083/products/past/profit"
    ).json()

    print("RESPONSE-1: ", response_products_profit_market_01_json_list)
    print("RESPONSE-2: ", response_products_profit_market_02_json_list)
    print("RESPONSE-3: ", response_products_profit_market_03_json_list)

    top_profit_products_uuid_list = []
    top_profit_products_uuid_list.extend(response_products_profit_market_01_json_list)
    top_profit_products_uuid_list.extend(response_products_profit_market_02_json_list)
    top_profit_products_uuid_list.extend(response_products_profit_market_03_json_list)

    # Sort by key "profit" and get the first 5
    print("TOP PROFIT: ", top_profit_products_uuid_list)

    # Parse all to items key profit_percentage to double round 2 decimales
    for product in top_profit_products_uuid_list:
        product["profit_percentage"] = round(product["profit_percentage"], 2)

    sorted_top_profit_products_uuid_list = sorted(
        top_profit_products_uuid_list,
        key=lambda x: x["profit_percentage"],
        reverse=True,
    )

    most_top_profit_products_uuid_list = sorted_top_profit_products_uuid_list[:5]

    print("TOP PROFIT: ", most_top_profit_products_uuid_list)

    # First show the most profitable products and then the most liked products
    top_final_products = most_top_profit_products_uuid_list + top_likes_product
    map_products = product_service.map_products_json_list(top_final_products)

    return filter_current_month_elements(map_products)


def calculate_top_categories():
    list_top_products = calculate_top_products()
    return [product["category_id"] for product in list_top_products]


def update_global_variables():
    global TOP_LIKES_AVERAGE, CATEGORIES_TOP, PRODUCTS_TOP
    TOP_LIKES_AVERAGE = calculate_like_average()
    CATEGORIES_TOP = calculate_top_categories()
    PRODUCTS_TOP = calculate_top_products()

    print("NEW top likes: ", TOP_LIKES_AVERAGE)
    print("NEW top categories: ", CATEGORIES_TOP)
    print("NEW top products: ", PRODUCTS_TOP)


##########------- Starting scheduler with server ---------#######


def calculate_daily_tasks():
    print("Daily execution for calculate tops", datetime.now())
    update_global_variables()


# calculate_daily_tasks()

scheduler = BackgroundScheduler()
trigger = CronTrigger(hour=3, minute=0)
scheduler.add_job(calculate_daily_tasks, trigger)
scheduler.start()


@app.on_event("shutdown")
def shutdown_event():
    scheduler.shutdown()


#####################----------------#####################


@app.get(
    "/like/{uuid}/email/{email_user}",
    description="Like a product only one time per user, return true if liked or false if was liked before",
)
def like_product(uuid: str, email_user: str):
    product_id = find_actual_product_by_uuid_past(uuid)

    if product_id is None:
        return {"result": False}

    boolean_result = product_service.like_product(product_id, email_user)

    return {"result": boolean_result}


@app.get(
    "/unlike/{uuid}/email/{email_user}",
    description="Unlike a product only one time per user, true if unliked or false if was unliked before",
)
def unlike_product(uuid: str, email_user: str):
    product_id = find_actual_product_by_uuid_past(uuid)

    if product_id is None:
        return {"result": False}

    boolean_result = product_service.unlike_product(product_id, email_user)

    return {"result": boolean_result}


@app.get(
    "/reaction/email/{email_user}/product/id/{uuid}",
    description="Check if a user has liked a product, returns 'liked', 'unliked' or 'none'",
)
def get_reaction(email_user: str, uuid: str):
    product_id = find_actual_product_by_uuid_past(uuid)

    if product_id is None:
        return {"reaction": "Product not found"}

    reaction = product_service.get_reaction(email_user, product_id)
    return {"reaction": reaction}


@app.get(
    "/start_month/password/{password}",
    description="Start the database with likes in the same month we are, this will reset all the likes, only is used when scrapping monthly",
)
def start_likes_database(password: str):
    if password == "wewiza":
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

    return {"message": "Wrong password"}


# https://127.0.0.2/suggest/id/36e79b3-4806-492c-ba2c-877395fc2ae5?filter_market=ahorramas
@app.get("/suggest/id/{uuid}")
def get_suggest_products(uuid: str, filter_markets: List[str] = Query(...)):

    list_all_products_user = []
    most_actual_uuid = find_actual_product_by_uuid_past(uuid)

    # Check in all market to get the complete data
    response_market_01 = requests.get(
        "http://api_market_01:8081/product/id/" + most_actual_uuid
    )
    response_market_02 = requests.get(
        "http://api_market_02:8082/product/id/" + most_actual_uuid
    )
    response_market_03 = requests.get(
        "http://api_market_03:8083/product/id/" + most_actual_uuid
    )

    list_all_products_user.append(response_market_01.json())
    list_all_products_user.append(response_market_02.json())
    list_all_products_user.append(response_market_03.json())

    # Clear None items
    list_all_products_user = list(filter(None, list_all_products_user))

    if len(list_all_products_user) == 0:
        return []

    uuid_searched = list_all_products_user[0]["uuid"]

    products_user_to_add_suggestions_list = []

    for market_name in filter_markets:
        for product_user in list_all_products_user:
            if market_name.lower().strip() == "mercadona":
                list_products_similar = requests.get(
                    f"http://api_market_01:8081/product/similar/name/{product_user['name']}"
                )
            elif market_name.lower().strip() == "ahorramas":
                list_products_similar = requests.get(
                    f"http://api_market_02:8082/product/similar/name/{product_user['name']}"
                )
            elif market_name.lower().strip() == "carrefour":
                list_products_similar = requests.get(
                    f"http://api_market_03:8083/product/similar/name/{product_user['name']}"
                )
            else:
                continue

            cheaper_products_suggestion = [
                product_suggestion
                for product_suggestion in list_products_similar.json()
                if product_suggestion.get("price_by_standard_measure", float("inf"))
                < product_user.get("price_by_standard_measure", float("inf"))
            ]

            products_user_to_add_suggestions_list.extend(cheaper_products_suggestion)

    # Clear None items
    products_user_to_add_suggestions_list = list(
        filter(None, products_user_to_add_suggestions_list)
    )

    valid_products = [
        x
        for x in products_user_to_add_suggestions_list
        if "price_by_standard_measure" in x
    ]

    # Filter products that not match with actual year and month
    valid_products = filter_current_month_elements(valid_products)

    # Delete prodcuts that have uuid or uuid_searched, to not repeat them, only get the suggest
    valid_products = list(filter(lambda x: x["uuid"] != uuid, valid_products))
    valid_products = list(filter(lambda x: x["uuid"] != uuid_searched, valid_products))
    valid_products = list(
        filter(lambda x: x["uuid"] != most_actual_uuid, valid_products)
    )

    products_user_to_add_suggestions_list = sorted(
        valid_products,
        key=lambda x: x["price_by_standard_measure"],
        reverse=False,
    )

    return products_user_to_add_suggestions_list[:3]


@app.get("/calculate/topics")
def calculate_topics():
    update_global_variables()

    return {
        "result": "Topics updated it going to take serveral minutes to show results"
    }


"""
@app.get(
    "/products",
    description="Get all products in the current month",
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
"""

"""
@app.get(
    "/update/measure_carrefour",
)
def update():
    response = requests.get("http://api_market_03:8083/update/measure_carrefour")
    return response.json()
"""

"""
@app.get("/update/minor_random_price")
def update_to_random_price_less():
    response_1 = requests.get("http://api_market_01:8081/update/minor_random_price")
    response_2 = requests.get("http://api_market_02:8082/update/minor_random_price")
    response_3 = requests.get("http://api_market_03:8083/update/minor_random_price")
    return {
        "result": "Result from all markets: "
        + str(response_1.json())
        + " "
        + str(response_2.json())
        + " "
        + str(response_3.json())
    }
"""
"""
@app.get("/update/zero")
def update_zero():
    # call all markets
    response_1 = requests.get("http://api_market_01:8081/update/zero")
    response_2 = requests.get("http://api_market_02:8082/update/zero")
    return {response_1}
"""
