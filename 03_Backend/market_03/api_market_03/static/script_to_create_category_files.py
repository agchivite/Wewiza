# TODO: change to CARREFOUR

elementos = [
    "aceite_especias_y_salsas",
    "agua_y_refrescos",
    "aperitivos",
    "arroz_legumbres_y_pasta",
    "azucar_caramelos_y_chocolate",
    "bebe",
    "bodega",
    "cacao_cafe_e_infusiones",
    "carne",
    "cereales_y_galletas",
    "charcuteria_y_quesos",
    "congelados",
    "conservas_caldos_y_cremas",
    "frutas",
    "huevos_leche_batidos_y_mantequilla",
    "marisco_y_pescado",
    "mascotas",
    "panaderia_y_pasteleria",
    "pizzas_y_platos_preparados",
    "postres_y_yogures",
    "verduras",
    "zumos",
]

# "cuidado_del_cabello",
# "cuidado_facial_y_corporal",
# "fitoterapia_y_parafarmacia",
# "limpieza_y_hogar",
# "maquillaje",

output_folder = "ahorramas_categories_ids_to_scrap"
import os

if not os.path.exists(output_folder):
    os.makedirs(output_folder)

for elemento in elementos:
    output_file = f"{output_folder}/{elemento}.csv"

    with open(output_file, "a", encoding="utf-8") as file:
        file.write(f"Este es el archivo para el elemento: {elemento}\n")
