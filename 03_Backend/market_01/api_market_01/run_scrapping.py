from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from api_market_01.src.services.scrapping_service import ScrappingService
from api_market_01.src.services.product_service import ProductService
from api_market_01.src.database.database_manager import DatabaseManager
from api_market_01.src.repositories.product_repository import ProductRepository

CONNECTION_MONGO = "mongodb://root:root@localhost:27021"
DATABASE_NAME = "mercadona"
COLLECTION_NAME = "products"

# Chrome Service
service = Service()
options = webdriver.ChromeOptions()
driver_chrome = webdriver.Chrome(service=service, options=options)

# Fachade
database_manager = DatabaseManager(CONNECTION_MONGO, DATABASE_NAME)
product_repository = ProductRepository(database_manager, COLLECTION_NAME)

# Services
product_service = ProductService(product_repository)
scrapping_service = ScrappingService(driver_chrome, product_service)

dictionaty_titles_categories = {}

### aceite_especias_y_salsas ###
list_aceite_especias_y_salsas = [
    "Aceite, vinagre y sal",
    "Especias y salsas",
    "Mayonesa, ketchup y mostaza",
    "Otras salsas",
]
dictionaty_titles_categories["aceite_especias_y_salsas"] = list_aceite_especias_y_salsas

### agua_y_refrescos ###
list_agua_y_refrescos = [
    "Agua",
    "Isotónico y energético",
    "Refresco de cola",
    "Refresco de naranja y de limón",
    "Tónica y bitter",
    "Refresco de té y sin gas",
]
dictionaty_titles_categories["agua_y_refrescos"] = list_agua_y_refrescos

### aperitivos ###
list_aperitivos = [
    "Aceitunas y encurtidos",
    "Frutos secos y fruta desecada",
    "Patatas fritas y snacks",
]
dictionaty_titles_categories["aperitivos"] = list_aperitivos

### arroz_legumbres_y_pasta ###
list_arroz_legumbres_y_pasta = ["Arroz", "Legumbres", "Pasta y fideos"]
dictionaty_titles_categories["arroz_legumbres_y_pasta"] = list_arroz_legumbres_y_pasta

### azucar_caramelos_y_chocolate ###
list_azucar_caramelos_y_chocolate = [
    "Azúcar y edulcorante",
    "Chicles y caramelos",
    "Chocolate",
    "Golosinas",
    "Mermelada y miel",
]
dictionaty_titles_categories["azucar_caramelos_y_chocolate"] = (
    list_azucar_caramelos_y_chocolate
)

### bebe ###
list_bebe = [
    "Alimentación infantil",
    "Biberón y chupete",
    "Higiene y cuidado",
    "Toallitas y pañales",
]
dictionaty_titles_categories["bebe"] = list_bebe

### bodega ###
list_bodega = [
    "Cerveza",
    "Cerveza sin alcohol",
    "Licores",
    "Sidra y cava",
    "Tinto de verano y sangría",
    "Vino blanco",
    "Vino lambrusco y espumoso",
    "Vino rosado",
    "Vino tinto",
]
dictionaty_titles_categories["bodega"] = list_bodega

### cacao_cafe_e_infusiones ###
list_cacao_cafe_e_infusiones = [
    "Cacao soluble y chocolate a la taza",
    "Café cápsula y monodosis",
    "Café molido y en grano",
    "Café soluble y otras bebidas",
    "Té e infusiones",
]
dictionaty_titles_categories["cacao_cafe_e_infusiones"] = list_cacao_cafe_e_infusiones

### carne ###
list_carne = [
    "Arreglos",
    "Aves y pollo",
    "Carne congelada",
    "Cerdo",
    "Conejo y cordero",
    "Embutido",
    "Hamburguesas y picadas",
    "Vacuno",
    "Empanados y elaborados",
]
dictionaty_titles_categories["carne"] = list_carne

### cereales_y_galletas ###
list_cereales_y_galletas = ["Cereales", "Galletas", "Tortitas"]
dictionaty_titles_categories["cereales_y_galletas"] = list_cereales_y_galletas

### charcuteria_y_quesos ###
list_charcuteria_y_quesos = [
    "Aves y jamón cocido",
    "Bacón y salchichas",
    "Chopped y mortadela",
    "Embutido curado",
    "Jamón serrano",
    "Paté y sobrasada",
    "Queso curado, semicurado y tierno",
    "Queso lonchas, rallado y en porciones",
    "Queso untable y fresco",
]
dictionaty_titles_categories["charcuteria_y_quesos"] = list_charcuteria_y_quesos

### congelados ###
list_congelados = [
    "Arroz y pasta",
    "Carne",
    "Helados",
    "Hielo",
    "Marisco",
    "Pescado",
    "Pizzas",
    "Rebozados",
    "Tartas y churros",
    "Verdura",
]
dictionaty_titles_categories["congelados"] = list_congelados

### conservas_caldos_y_cremas ###
list_conservas_caldos_y_cremas = [
    "Atún y otras conservas de pescado",
    "Berberechos y mejillones",
    "Conservas de verdura y frutas",
    "Gazpacho y cremas",
    "Sopa y caldo",
    "Tomate",
]
dictionaty_titles_categories["conservas_caldos_y_cremas"] = (
    list_conservas_caldos_y_cremas
)

### frutas ###
list_frutas = ["Fruta"]
dictionaty_titles_categories["frutas"] = list_frutas

### verduras ###
list_verduras = ["Lechuga y ensalada preparada", "Verdura"]
dictionaty_titles_categories["verduras"] = list_verduras

### huevos_leche_batidos_y_mantequilla ###
list_huevos_leche_batidos_y_mantequilla = [
    "Huevos",
    "Leche y bebidas vegetales",
    "Mantequilla y margarina",
]
dictionaty_titles_categories["huevos_leche_batidos_y_mantequilla"] = (
    list_huevos_leche_batidos_y_mantequilla
)

### marisco_y_pescado ###
list_marisco_y_pescado = [
    "Marisco",
    "Pescado congelado",
    "Pescado fresco",
    "Salazones y ahumados",
    "Sushi",
]
dictionaty_titles_categories["marisco_y_pescado"] = list_marisco_y_pescado

### mascotas ###
list_mascotas = ["Gato", "Perro", "Otros"]
dictionaty_titles_categories["mascotas"] = list_mascotas

### panaderia_y_pasteleria ###
list_panaderia_y_pasteleria = [
    "Bollería de horno",
    "Bollería envasada",
    "Harina y preparado repostería",
    "Pan de horno",
    "Pan de molde y otras especialidades",
    "Pan tostado y rallado",
    "Picos, rosquilletas y picatostes",
    "Tartas y pasteles",
    "Velas y decoración",
]
dictionaty_titles_categories["panaderia_y_pasteleria"] = list_panaderia_y_pasteleria

### pizzas_y_platos_preparados ###
list_pizzas_y_platos_preparados = [
    "Listo para Comer",
    "Pizzas",
    "Platos preparados calientes",
    "Platos preparados fríos",
]
dictionaty_titles_categories["pizzas_y_platos_preparados"] = (
    list_pizzas_y_platos_preparados
)

### postres_y_yogures ###
list_postres_y_yogures = [
    "Bífidus",
    "Flan y natillas",
    "Gelatina y otros postres",
    "Postres de soja",
    "Yogures desnatados",
    "Yogures griegos",
    "Yogures líquidos",
    "Yogures naturales y sabores",
    "Yogures y postres infantiles",
]
dictionaty_titles_categories["postres_y_yogures"] = list_postres_y_yogures

### zumos ###
list_zumos = ["Fruta variada", "Melocotón y piña", "Naranja", "Tomate y otros sabores"]
dictionaty_titles_categories["zumos"] = list_zumos

list_num_categories = [
    105,
    110,
    111,
    106,
    103,
    109,
    108,
    104,
    107,
    98,
    143,
    100,
    99,
    142,
    140,
    138,
    897,
    68,
    64,
    62,
    60,
    59,
    69,
    66,
    65,
    71,
    222,
    221,
    225,
    789,
    36,
    31,
    34,
    32,
    75,
    72,
    77,
    28,
    29,
    27,
    126,
    129,
    130,
    127,
    123,
    122,
    145,
    152,
    884,
    151,
    149,
    150,
    155,
    154,
    148,
    147,
    53,
    56,
    54,
    58,
    50,
    51,
    49,
    52,
    48,
    79,
    80,
    78,
    45,
    40,
    44,
    43,
    42,
    37,
    47,
    38,
    46,
    88,
    84,
    83,
    81,
    86,
    164,
    166,
    181,
    174,
    168,
    170,
    173,
    171,
    169,
    216,
    219,
    218,
    217,
    89,
    95,
    92,
    97,
    90,
    112,
    115,
    116,
    117,
    156,
    163,
    158,
    159,
    161,
    162,
    135,
    133,
    132,
    118,
    121,
    120,
]
scrapping_service.run_scrapping_mercadona(
    list_num_categories, dictionaty_titles_categories
)
