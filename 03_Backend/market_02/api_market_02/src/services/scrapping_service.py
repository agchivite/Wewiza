import json
import uuid
from selenium.webdriver.chrome.service import Service
from bs4 import BeautifulSoup
from api_market_02.src.models.product import Product
from api_market_02.src.services.product_service import ProductService
import re
import os
import requests


class ScrappingService:
    def __init__(self, driver, product_service: ProductService):
        self.driver = driver
        self.product_service = product_service
        self.output_folder = "log_error"

    def run_simulation(self):
        categories_dict = {
            "aperitivos": ["patatas_fritas", "gourmet", "lisas"],
            "zumos": ["zumos", "naranja", "pina"],
        }

        """
        # TODO: Change to get static from relative path
        categories_folder = "C:\\Users\\chivi\\Desktop\\git-ephemeral\\wewiza\\03_Backend\\market_02\\api_market_02\\static\\ahorramas_categories_ids_to_scrap"


        for category_id_wewiza in os.listdir(categories_folder):
            file_path = os.path.join(categories_folder, category_id_wewiza)

            if os.path.isfile(file_path):
                with open(file_path, "r") as f:
                    contenido = f.read()

                categories = contenido.split(";")
                categories_dict[category_id_wewiza] = categories
        """

        self.scrap_categories(categories_dict)

    def scrap_categories(self, categories_dict):
        # Only to keep a log when fail scrapping, because is imposible to check the failures in the console with a lot of products
        if not os.path.exists(self.output_folder):
            os.makedirs(self.output_folder)

        for category_id_wewiza, categories_list in categories_dict.items():
            for categorie_ahorramas in categories_list:
                url = f"https://www.ahorramas.com/on/demandware.store/Sites-Ahorramas-Site/es/Search-UpdateGrid?cgid={categorie_ahorramas}&pmin=0.01&start=0&sz=1500"

                response = requests.get(url)
                if response.status_code == 200:
                    print(f"Accediendo a la página: {url}")

                    output_file = f"output{categorie_ahorramas}.txt"
                    self.driver.get(url)

                    try:
                        # Wait for the page to load
                        self.driver.implicitly_wait(20)

                        # Obtaining content page after JavaScript has loaded the data dynamically
                        page_source = self.driver.page_source
                        soup = BeautifulSoup(page_source, "html.parser")

                        # TODO: no me pilla bien los productos a partir de aquí...
                        products_html = soup.find_all("div", class_="product-tile  ")

                        for product_html in products_html:
                            product_model = self.map_product_html_to_model(
                                product_html, category_id_wewiza
                            )

                            if product_model.name != "[no-data]":
                                # TODO: change to server
                                """
                                self.send_to_wewiza_server(
                                    product_model, category_id_wewiza
                                )
                                """
                                self.send_to_localhost_mongo(product_model)
                            else:
                                print("Can not retrieve product data.")

                    except Exception as e:
                        print(f"Error al procesar la página: {e}")
                        output_file = (
                            f"{self.output_folder}/output_{category_id_wewiza}.txt"
                        )
                        self.write_error_to_file(
                            f"Error al procesar la página: {e}", output_file
                        )
                else:
                    print(f"La página no existe: {url}")
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

    def scrap_quantity_product_link(self, product_html):
        try:
            product_link = product_html.find("a", class_="product-pdp-link")

            if product_link:
                href = product_link.get("href")

                if href.startswith("http"):
                    full_url = "https://www.ahorramas.com" + href
                    self.driver.get(full_url)
                    return self.scrap_quantity_from_new_page()
                else:
                    print("El enlace no es válido.")
                    return 0.0
            else:
                print("No se encontró el enlace del producto.")
        except Exception as e:
            print(f"Error al abrir el enlace del producto: {e}")

    def scrap_quantity_from_new_page(self):
        try:
            three_paragraphs = self.driver.find_all("div", class_="collapse show")

            only_we_get_the_second_paragraph = 0
            for parraph in three_paragraphs:
                only_we_get_the_second_paragraph += 1
                if only_we_get_the_second_paragraph == 2:
                    p = parraph.find("p").text
                    p = (
                        p.strip()
                        .replace(":", "")
                        .replace("KG", "")
                        .strip()
                        .replace(",", ".")
                    )
                    return float(p)

        except Exception as e:
            print(f"Error al obtener la cantidad del producto en la nueva página: {e}")
            return 0.0

    def map_product_html_to_model(self, product_html, id_category):
        try:
            name = product_html.find("h2", class_="link product-name-gtm").text

            price = 0.0
            price = product_html.find("span", class_="value").text

            possible_measure = product_html.find(
                "span", class_="units js-selector-units"
            ).text
            measure = "Failed to find measurement"

            possibles_measures = ["Kg", "KILO", "gr", "L", "l", "LITRO" "ml", "DOCENA"]

            if possible_measure in possibles_measures:
                measure = possible_measure

            quantity_measure = 0.0
            # TODO: descomentar
            # quantity_measure = self.scrap_quantity_product_link(product_html)

            price_per_measure = 0.0
            if quantity_measure != 0.0:
                price_per_measure = self.calculate_price_per_measure(
                    quantity_measure, measure, price, name
                )

            image_wrapper = product_html.find("div", class_="image-container")
            image_url = (
                image_wrapper.find("img")["src"] if image_wrapper else "[no-image]"
            )

            only_numbers_price = re.sub(r"[^\d.,]", "", price)
            only_numbers_price = only_numbers_price.replace(",", ".")
            price_float = float(only_numbers_price)

            store_name = "Ahorramas"
            store_image_url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRlu7l3PhGxyJUajK_-O_CQoAaPiOy_kDxdpYm7Gy-n2A&s"

            measure = measure.lower()
            if measure == "gr":
                measure = "g"
            if measure == "kilo":
                measure = "kg"
            if measure == "litro":
                measure = "l"
            if measure == "docena":
                measure = "ud."

            product = Product(
                str(uuid.uuid4()),
                id_category,
                name,
                price_float,
                float(quantity_measure),
                measure,
                price_per_measure,
                image_url,
                store_name,
                store_image_url,
            )

            print(product)
        except AttributeError as e:
            print(f"Error al obtener datos del producto: {e}")
            output_file = f"{self.output_folder}/output_{id_category}.txt"
            self.write_error_to_file(
                f"Error al obtener datos del producto: {e}", output_file
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
            )

        return product

    def calculate_price_per_measure(
        self, quantity, measure, price_per_measure, name_product
    ):
        splited_euro = price_per_measure.split(" ")
        price_per_measure = float(splited_euro[0].replace(",", "."))

        measure = measure.lower()

        if measure == "gr":
            quantity_in_kg = quantity / 1000
            price_per_kg = price_per_measure / quantity_in_kg
            return price_per_kg
        elif measure == "kg" or measure == "kilo":
            price_per_kg = price_per_measure / quantity
            return price_per_kg
        elif measure == "ml":
            quantity_in_liter = quantity / 1000
            price_per_liter = price_per_measure / quantity_in_liter
            return price_per_liter
        elif measure == "l" or measure == "litro":
            price_per_liter = price_per_measure / quantity
            return price_per_liter
        elif measure == "docena" and "huevo" in name_product.lower():
            quantity_in_dozen = quantity / 12
            return price_per_measure / quantity_in_dozen
        elif measure == "ud.":
            # Other cases with ud., it going to calculate with standard ud. as 1
            return price_per_measure / quantity
        else:
            return "Measure is not valid"

    def write_error_to_file(self, error, output_file):
        with open(output_file, "a", encoding="utf-8") as file:
            file.write(error + "\n")
