from fastapi import FastAPI, BackgroundTasks
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
    "/product/id/{product_id}",
    description="Product details by id, if not found returns 'name': 'Product not found'",
)
def get_product_by_id(product_id: str):
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
    "/product/details/id/{product_id}",
    description="List of same product with different date_created, the latest one will be the main product, if not found returns empty list",
)
def get_product_details_by_id(product_id: str):
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

    top_profit_products_uuid_list = (
        response_products_profit_market_01_json_list
        + response_products_profit_market_02_json_list
        + response_products_profit_market_03_json_list
    )

    # Sort by key "profit" and get the first 10
    top_profit_products_uuid_list.sort(
        key=lambda x: x["profit_percentage"], reverse=True
    )
    top_profit_products_uuid_list = top_profit_products_uuid_list[:10]

    top_final_products = top_likes_product + top_profit_products_uuid_list
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


# CASE 1: start server
TOP_LIKES_AVERAGE = calculate_like_average()
CATEGORIES_TOP = calculate_top_categories()
PRODUCTS_TOP = calculate_top_products()
#####################----------------#####################


@app.get("/calculate/topics")
def calculate_topics(background_tasks: BackgroundTasks):
    background_tasks.add_task(update_global_variables)
    return {"result": "Topics updated it going to take some time to show in endpoints"}


@app.get(
    "/like/{product_id}/email/{email_user}",
    description="Like a product only one time per user, return true if liked or false if was liked before",
)
def like_product(product_id: str, email_user: str, background_tasks: BackgroundTasks):
    boolean_result = product_service.like_product(product_id, email_user)
    # CASE 2: like/unlike
    # background_tasks.add_task(update_global_variables)
    return {"result": boolean_result}


@app.get(
    "/unlike/{product_id}/email/{email_user}",
    description="Unlike a product only one time per user, true if unliked or false if was unliked before",
)
def unlike_product(product_id: str, email_user: str, background_tasks: BackgroundTasks):
    boolean_result = product_service.unlike_product(product_id, email_user)
    # CASE 2: like/unlike
    # background_tasks.add_task(update_global_variables)
    return {"result": boolean_result}


@app.get(
    "/reaction/email/{email_user}/product/id/{product_id}",
    description="Check if a user has liked a product, returns 'liked', 'unliked' or 'none'",
)
def get_reaction(email_user: str, product_id: str):
    reaction = product_service.get_reaction(email_user, product_id)
    return {"reaction": reaction}


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

    # CASE 3: start new month
    global TOP_LIKES_AVERAGE, CATEGORIES_TOP, PRODUCTS_TOP
    TOP_LIKES_AVERAGE = calculate_like_average()
    CATEGORIES_TOP = calculate_top_categories()
    PRODUCTS_TOP = calculate_top_products()

    return {"message": "Database likes updated"}


@app.get(
    "/update/measure_carrefour",
)
def update():
    response = requests.get("http://api_market_03:8083/update/measure_carrefour")
    return response.json()
