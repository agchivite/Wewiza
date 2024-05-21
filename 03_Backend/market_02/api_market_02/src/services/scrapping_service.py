import json
import uuid
from selenium.webdriver.chrome.service import Service
from python_on_rails.result import Result
from bs4 import BeautifulSoup
from api_market_02.src.models.product import Product
from api_market_02.src.services.product_service import ProductService
import re
import os
import requests
from datetime import datetime


class ScrappingService:
    def __init__(self, driver, product_service: ProductService):
        self.driver = driver
        self.product_service = product_service
        self.output_folder = ""

    def run_scrapping_ahorramas(self):
        current_directory = os.path.dirname(os.path.realpath(__file__))

        api_market_02_folder = os.path.abspath(
            os.path.join(current_directory, "..", "..")
        )
        log_error_folder = os.path.join(api_market_02_folder, "log_error_ahorramas")
        self.output_folder = log_error_folder

        # Only to keep a log when fail scrapping, because is imposible to check the failures in the console with a lot of products
        if not os.path.exists(log_error_folder):
            os.makedirs(log_error_folder)

        current_directory = os.path.dirname(os.path.realpath(__file__))
        static_folder = os.path.abspath(
            os.path.join(current_directory, "..", "..", "static")
        )
        categories_folder = os.path.join(
            static_folder, "ahorramas_categories_ids_to_scrap"
        )

        categories_dict = {}

        for category_id_wewiza in os.listdir(categories_folder):
            file_path = os.path.join(categories_folder, category_id_wewiza)

            if os.path.isfile(file_path):
                with open(file_path, "r") as f:
                    contenido = f.read()

                categories = contenido.split(";")
                category_id_wewiza = category_id_wewiza.replace(".csv", "")
                categories_dict[category_id_wewiza] = categories

        self.scrap_categories(categories_dict)

    def scrap_categories(self, categories_dict):
        for category_id_wewiza, categories_list in categories_dict.items():
            for categorie_ahorramas in categories_list:
                url = f"https://www.ahorramas.com/on/demandware.store/Sites-Ahorramas-Site/es/Search-UpdateGrid?cgid={categorie_ahorramas}&pmin=0.01&start=0&sz=1500"

                response = requests.get(url)
                if response.status_code == 200:
                    print(f"Accessing to page: {url}")

                    output_file = f"output{categorie_ahorramas}.txt"
                    self.driver.get(url)

                    try:
                        # Wait for the page to load
                        self.driver.implicitly_wait(20)

                        # Obtaining content page after JavaScript has loaded the data dynamically
                        page_source = self.driver.page_source
                        soup = BeautifulSoup(page_source, "html.parser")

                        products_html = soup.find_all("div", class_="product-tile")

                        for product_html in products_html:
                            try:
                                product_model = self.map_product_html_to_model(
                                    product_html, category_id_wewiza
                                )

                                if product_model.name != "[no-data]":
                                    # TODO: change when need it

                                    self.send_to_wewiza_server(
                                        product_model, category_id_wewiza
                                    )

                                    # self.send_to_localhost_mongo(product_model)
                                else:
                                    print("Can not retrieve product data.")
                            except Exception as e:
                                print(f"Error processing product: {e}")
                                continue

                    except Exception as e:
                        print(f"Error to process page: {e}")
                        output_file = (
                            f"{self.output_folder}/output_{category_id_wewiza}.txt"
                        )
                        self.write_error_to_file(
                            f"Error to process page: {e}", output_file
                        )
                else:
                    print(f"Page not found: {url}")
        self.driver.quit()

    def send_to_localhost_mongo(self, product_model: Product):
        product_dict = product_model.dict()
        json_string = json.dumps(product_dict, indent=4)
        self.product_service.create_product_to_mongo_recieving_json(json_string)

    def send_to_wewiza_server(self, product_model: Product, id_category):
        product_dict = product_model.dict()
        json_string = json.dumps(product_dict, indent=4)

        response = requests.post(
            "http://wewiza.ddns.net:82/insert_new_scrapped_product",
            json=json_string,
        )
        if response.status_code == 200:
            print(
                f"Product '{product_model.name}' inserted successfully to wewiza-server."
            )
        else:
            print(f"Error inserting product to wewiza-server: {response.text}")
            output_file = f"{self.output_folder}/output_{id_category}.txt"
            self.write_error_to_file(
                f"Error inserting product to wewiza-server: {response.text}",
                output_file,
            )

    def map_product_html_to_model(self, product_html, id_category):
        try:
            url = (
                "https://www.ahorramas.com/"
                + product_html.find("a", class_="product-pdp-link")["href"]
            )

            name = product_html.find("h2", class_="link product-name-gtm").text

            price = 0.0
            price = product_html.find("span", class_="value").text

            """
            Return ej: 500 g
            """
            quantity_measure = 0.0
            quantity_measure_and_measure_first_chance_from_name = (
                self.filter_quantity_measure_and_measure_first_chance_from_name(
                    name, id_category
                )
            ).split(" ")

            "Format price with second chance measure: (3,19€/DOCENA)"
            price_per_measure_standard_with_measure_second_chance = 0.0

            # Checking if has offer
            is_product_offer = product_html.find(
                "span", class_="unit-price-per-unit red"
            )
            if is_product_offer:
                price_per_measure_standard_with_measure_second_chance = (
                    is_product_offer.text
                )
            else:
                price_per_measure_standard_with_measure_second_chance = (
                    product_html.find("span", class_="unit-price-per-unit grey").text
                )

            price_per_measure_standard_with_measure_second_chance = (
                price_per_measure_standard_with_measure_second_chance.replace(" ", "")
                .replace("€", "")
                .replace("/", " ")
                .replace("(", "")
                .replace(")", "")
                .replace(",", ".")
                .replace("\n", "")
                .strip()
                .split(" ")
            )

            price_per_standard_measure = float(
                price_per_measure_standard_with_measure_second_chance[0]
            )
            possible_measure_second_chance = (
                price_per_measure_standard_with_measure_second_chance[1]
            )

            measure = "kg"
            if quantity_measure_and_measure_first_chance_from_name[0] != "0.0":
                quantity_measure = quantity_measure_and_measure_first_chance_from_name[
                    0
                ].replace(",", ".")

                if "num_pack_" in quantity_measure:
                    # In case we have num_pack, we need to calculate his quantity for standard kg
                    parts = quantity_measure.split("_")
                    quantity_measure = price / int(parts[2])
                elif quantity_measure == "none":
                    # In case we don't have quantity, we detect the price is for standard kg
                    quantity_measure = "1"

                measure = quantity_measure_and_measure_first_chance_from_name[1]

            else:
                # In case from name we could not find the quantity and measurement, we use the (possible_measure_second_chance)
                quantity_measure = "0.0"
                possibles_measures_second_chance = [
                    "KILO",
                    "LITRO",
                    "KG.PESO ESC",
                    "DOCENA",
                    "UNIDAD",
                ]
                if possible_measure_second_chance in possibles_measures_second_chance:
                    measure = possible_measure_second_chance
                if measure == "KILO":
                    measure = "kg"
                if measure == "LITRO":
                    measure = "l"
                if measure == "KG.PESO ESC":
                    measure = "kg"
                if measure == "DOCENA":
                    measure = "ud."
                if measure == "UNIDAD":
                    measure = "ud."

            image_wrapper = product_html.find("div", class_="image-container")
            image_url = (
                image_wrapper.find("img")["src"] if image_wrapper else "[no-image]"
            )

            only_numbers_price = re.sub(r"[^\d.,]", "", price)
            only_numbers_price = only_numbers_price.replace(",", ".")
            price_float = float(only_numbers_price)

            store_name = "Ahorramas"
            store_image_url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRlu7l3PhGxyJUajK_-O_CQoAaPiOy_kDxdpYm7Gy-n2A&s"

            if quantity_measure == "0.0":
                quantity_measure = 1

            current_date = datetime.now()
            current_date_str = current_date.strftime("%Y-%m-%d %H:%M:%S")
            # TODO: scrap one time with april and less price per standard measure
            """ 
            current_date_str = (
                "2024-04-05 00:00:00"
            )
            """
            product = Product(
                str(uuid.uuid4()),
                id_category,
                name,
                price_float,
                float(quantity_measure),
                measure,
                price_per_standard_measure,
                image_url,
                url,
                store_name,
                store_image_url,
                current_date_str,
            )

        except AttributeError as e:
            print(f"Error to obtain data from product: {e}")
            output_file = f"{self.output_folder}/output_{id_category}.txt"
            self.write_error_to_file(
                f"Error to obtain data from product: {e}", output_file
            )
            no_data = "[no-data]"
            product = Product(
                no_data,
                no_data,
                no_data,
                0.0,
                0,
                no_data,
                0.0,
                no_data,
                no_data,
                no_data,
                no_data,
                no_data,
            )

        return product

    def filter_quantity_measure_and_measure_first_chance_from_name(
        self, name, id_category
    ):
        try:
            """
            Posibles returns:
            - 500 g -> In normal case
            - num_pack_X kg -> In case we dont have the measure
            - none kg -> In case we don't anything
            """
            possibles_names_to_study_web = [
                """
                - 1.5l pack 2
                - 1l
                - 750ml
                - 0.25l

                - 100g pack 2
                - 310g
                - 100 gr
                - 500G
                - 1.5kg + 1kg
                - 1.5kg

                # En estos casos será:
                - Si no pone nada directamente es al kilo estandar... -> return none kg
                - Si no pone nada pero si pack Xnum (en este caso es mejor poner al kilo estandar) -> return num_pack_X kg
                - 3uds Poner al kilo estandar -> return num_pack_X kg
                """
            ]
            result_kg = self.kilogrames_filter_quantity_measure_and_measure_first_chance_from_name(
                name
            )
            if result_kg.value != None:
                return result_kg.value

            result_l = (
                self.litres_filter_quantity_measure_and_measure_first_chance_from_name(
                    name
                )
            )
            if result_l.value != None:
                return result_l.value

            result_egg = (
                self.eggs_filter_quantity_measure_and_measure_first_chance_from_name(
                    name
                )
            )
            if result_egg.value != None:
                return result_egg.value

            return str(0.0) + " none"
        except AttributeError as e:
            print(f"Error filtering quantity and measure: {e}")
            output_file = f"{self.output_folder}/output_{id_category}.txt"
            self.write_error_to_file(
                f"Error filtering quantity and measure: {e}",
                output_file,
            )

    def eggs_filter_quantity_measure_and_measure_first_chance_from_name(self, name):
        # CASE EGGS
        egg_patterns = [r"(\d+)\s*unidades?", r"(\d+)\s*unid\b", r"(\d+)\s*u\b"]

        for pattern in egg_patterns:
            match = re.search(pattern, name)
            if match:
                return Result.success(f"{match.group(1)} ud.")

        # CASE GENERAL EGGS
        # In case then name has not the quantity eggs
        possible_eggs_standard_dozens_values = ["Huevos", "huevos"]

        for possible_eggs_standard_dozens_value in possible_eggs_standard_dozens_values:
            if possible_eggs_standard_dozens_value in name:
                return Result.success(f"{str(12)} ud.")

        return Result.failure("failure")

    def litres_filter_quantity_measure_and_measure_first_chance_from_name(self, name):
        liters_patterns = [
            r"(\d*[,\.]?\d+)\s*(l|ml|cl)\s*pack\s*(\d+)",
            r"(\d*[,\.]?\d+)\s*(l|ml|cl)",
        ]

        quantities = []
        units = set()

        quantity = 0
        found_pattern = False

        for pattern in liters_patterns:
            for match in re.finditer(pattern, name, re.IGNORECASE):
                quantity_str = match.group(1)
                quantity = float(quantity_str.replace(",", "."))
                unidad = match.group(2).lower()
                name_splited = name.split(" ")
                for i in range(len(name_splited)):
                    if "pack" in name_splited[i]:
                        if name_splited[i + 1].isdigit():
                            packs = int(name_splited[i + 1])
                            quantity *= packs

                units.add(unidad)
                quantities.append(quantity)

                if quantity != 0:
                    found_pattern = True
                    quantity = 0
            if found_pattern:
                break

        total_quantity = float(sum(quantities))

        if found_pattern:
            final_unit = units.pop()
            return Result.success(f"{total_quantity} {final_unit}")
        else:
            return Result.failure("failure")

    def kilogrames_filter_quantity_measure_and_measure_first_chance_from_name(
        self, name
    ):
        kilos_patterns = [
            r"(\d*[,\.]?\d+)\s*(g|gr|kg)\s*pack\s*(\d+)",
            r"(\d*[,\.]?\d+)\s*(g|gr|kg)",
        ]

        # We need beacuse there are cases with: 1kg +1,5kg
        quantities = []
        units = set()

        quantity = 0
        found_pattern = False

        for pattern in kilos_patterns:
            for match in re.finditer(pattern, name, re.IGNORECASE):
                quantity_str = match.group(1)
                quantity = float(quantity_str.replace(",", "."))
                unidad = match.group(2).lower()
                if unidad in ("gr"):
                    unidad = "g"
                name_splited = name.split(" ")
                for i in range(len(name_splited)):
                    if "pack" in name_splited[i] and name_splited[i + 2] == "de":
                        if name_splited[i + 1].isdigit():
                            packs = int(name_splited[i + 1])
                            quantity *= packs

                units.add(unidad)
                quantities.append(quantity)

                if quantity != 0:
                    found_pattern = True
                    quantity = 0
            if found_pattern:
                break

        total_quantity = float(sum(quantities))

        if found_pattern:
            final_unit = units.pop()
            return Result.success(f"{total_quantity} {final_unit}")
        else:
            return Result.failure("failure")

    def write_error_to_file(self, error, output_file):
        with open(output_file, "a", encoding="utf-8") as file:
            file.write(error + "\n")
