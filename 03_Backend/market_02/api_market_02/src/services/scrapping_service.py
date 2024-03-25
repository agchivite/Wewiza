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
        self.output_folder = "log_error_ahorramas"

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

    def scrap_quantity_product_link(self, product_html, id_category):
        try:
            product_link = product_html.find("a", class_="product-pdp-link")

            if product_link:
                href = product_link.get("href")
                single_product_full_url = "https://www.ahorramas.com" + href

                response = requests.get(single_product_full_url)
                if response.status_code == 200:
                    return self.scrap_quantity_from_new_page(
                        single_product_full_url, id_category
                    )
                else:
                    print("Single product link not recieved 200 response")
                    output_file = f"{self.output_folder}/output_{id_category}.txt"
                    self.write_error_to_file(
                        f"Single product link not recieved 200 response", output_file
                    )
                    return 0.0
            else:
                print("Single product link not found")
                output_file = f"{self.output_folder}/output_{id_category}.txt"
                self.write_error_to_file(f"Single product link not found", output_file)
        except Exception as e:
            print(f"Error to open single product link: {e}")
            output_file = f"{self.output_folder}/output_{id_category}.txt"
            self.write_error_to_file(
                f"Error to open single product link: {e}", output_file
            )

    def scrap_quantity_from_new_page(self, single_product_full_url, id_category):
        try:
            self.driver.get(single_product_full_url)
            page_source = self.driver.page_source
            soup = BeautifulSoup(page_source, "html.parser")

            div_containing_three_paragraphs = soup.find("div", class_="collapse show")
            paragraphs = div_containing_three_paragraphs.find_all("p")
            text_of_second_span = paragraphs[1].text

            quantity_text = (
                text_of_second_span.strip()
                .replace("\n", "")
                .replace("Peso Neto", "")
                .replace(":", "")
                .replace("KG", "")
                .strip()
                .replace(",", ".")
            )

            return float(quantity_text)
        except Exception as e:
            print(f"Error to obtain paragraphs from single product link: {e}")
            output_file = f"{self.output_folder}/output_{id_category}.txt"
            self.write_error_to_file(
                f"Error to obtain paragraphs from single product link: {e}", output_file
            )
            return 0.0

    def map_product_html_to_model(self, product_html, id_category):
        try:
            name = product_html.find("h2", class_="link product-name-gtm").text

            price = 0.0
            price = product_html.find("span", class_="value").text

            # TODO: puedo sacar del NAME con excepciones, a veces ponen:
            quantity_measure = 0.0
            quantity_measure = self.filter_quantity_measure_by_name(name)
            """
            quantity_measure = 
                - 1,5l pack 2
                - 100g pack 2
                - 1l
                - 750ml
                - 0,25l
                - 0.25l
                - 310g
                - 100 gr
                - 500G
                - 1,5kg + 1kg
                - 1.5kg
                - Si no pone nada directamente es al kilo...
                - 12 unidades (huevos)
                - 24u (huevos)
                - Ponene nada y.. pack 3 (en este caso es mejor poner al kilo) -> KG.PESO ESC = KG
                - 3uds (omitir tambien)
            """

            price_per_measure_and_measure = 0.0
            # if quantity_measure != 0.0:
            # First check if has offer
            is_product_offer = product_html.find(
                "span", class_="unit-price-per-unit red"
            )
            if is_product_offer:
                price_per_measure_and_measure = is_product_offer.text
            else:
                price_per_measure_and_measure = product_html.find(
                    "span", class_="unit-price-per-unit grey"
                ).text

            "Format coming from scrapping: (3,19€/DOCENA)"
            price_per_measure_and_measure = (
                price_per_measure_and_measure.replace(" ", "")
                .replace("€", "")
                .replace("/", " ")
                .replace("(", "")
                .replace(")", "")
                .replace(",", ".")
                .replace("\n", "")
                .strip()
                .split(" ")
            )
            price_per_measure = float(price_per_measure_and_measure[0])
            possible_measure = price_per_measure_and_measure[1]

            measure = "Failed to find measurement"

            # TODO: repasar los posibles measure, cambiar sacando del nombre el valor real, y si no dan valor cogemos el generañ de aquí
            possibles_measures = ["KILO", "LITRO", "KG.PESO ESC", "DOCENA", "UNIDAD"]

            if possible_measure in possibles_measures:
                measure = possible_measure

            # TODO: repasar los posibles measure, cambiar sacando del nombre el valor real, y si no dan valor cogemos el generañ de aquí
            measure = measure.lower()
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
            )

        return product

    def write_error_to_file(self, error, output_file):
        with open(output_file, "a", encoding="utf-8") as file:
            file.write(error + "\n")
