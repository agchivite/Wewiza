import json
import uuid
from selenium.webdriver.chrome.service import Service
from bs4 import BeautifulSoup
from api_market_01.src.models.product import Product
from api_market_01.src.services.product_service import ProductService
import re
import os
import requests


class ScrappingService:
    def __init__(self, driver, product_service: ProductService):
        self.driver = driver
        self.product_service = product_service

    def run_simulation(self, start_category, end_category):
        """
        output_folder = "data"
        if not os.path.exists(output_folder):
            os.makedirs(output_folder)
        """

        for i in range(start_category, end_category + 1):
            url = f"https://tienda.mercadona.es/categories/{i}"

            response = requests.get(url)
            if response.status_code == 200:
                print(f"Accediendo a la página: {url}")

                output_file = f"output{i}.txt"
                url = f"https://tienda.mercadona.es/categories/{i}"
                self.driver.get(url)

                try:
                    # Wait for the page to load
                    self.driver.implicitly_wait(20)

                    category_title_element = self.driver.find_element(
                        "css selector", ".category-detail__title.title1-b"
                    )
                    category_title = category_title_element.text.strip()
                    id_category = self.map_category_title_to_id(category_title)

                    # Obtaining content page after JavaScript has loaded the data dynamically
                    page_source = self.driver.page_source
                    soup = BeautifulSoup(page_source, "html.parser")

                    # Searching for a specific pattern that matches the 404 error message, to not search constantly
                    error_message = soup.find_all("div", class_="error-404")
                    if error_message and "404" in error_message.text:
                        print(f"La página no existe: {url}")
                    else:
                        products_html = soup.find_all("div", class_="product-cell")

                        for product_html in products_html:
                            product_model = self.map_product_html_to_model(
                                product_html, id_category
                            )

                            if product_model.name != "[no-data]":
                                self.send_to_wewiza_server(product_model)
                                # self.send_to_localhost_mongo(product_model)

                except Exception as e:
                    print(f"Error al procesar la página {i}: {e}")
                    # output_file = f"{output_folder}/output{i}.txt"
                    # write_products_to_file(products, output_file)
            else:
                print(f"La página no existe: {url}")

        self.driver.quit()

    def send_to_localhost_mongo(self, product_model):
        self.product_service.create_product_to_mongo_recieving_json(product_model)

    def send_to_wewiza_server(self, product_model: Product):
        product_dict = product_model.dict()
        json_string = json.dumps(product_dict, indent=4)

        response = requests.post(
            "http://wewiza.ddns.net:81/insert_new_scrapped_product",
            json=json_string,
        )
        if response.status_code == 200:
            print(
                f"Product '{product_model.name}' inserted successfully to wewiza-server."
            )
        else:
            print(f"Error inserting product to wewiza-server: {response.text}")

    def map_category_title_to_id(self, category_title):
        """
        All categories has a especific id_category pattern but Fruits and Vegetables has to be treated differently
        """
        if category_title == "Fruta":
            return "frutas"
        elif category_title == "Lechuga y ensalada preparada":
            return "verduras"
        elif category_title == "Verdura":
            return "verduras"
        else:
            return (
                category_title.replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace(" ", "_")
                .replace(",", "")
                .lower()
            )

    # TODO: make a mapper DTO
    def map_product_html_to_model(self, product_html, id_category):
        try:
            name = product_html.find(
                "h4", class_="subhead1-r product-cell__description-name"
            ).text
            price = product_html.find(
                "p", class_="product-price__unit-price subhead1-b"
            ).text

            # Sometimes the tag "footnote1-r" appears twice, so I join all in one
            footnote1_r_tags = product_html.find_all("span", class_="footnote1-r")
            texts = []
            for tag in footnote1_r_tags:
                text = tag.get_text(
                    strip=True
                )  # Tag text without spaces at the beginning and end
                texts.append(text)

            footnote1_r_in_one_text_by_spaces = " ".join(texts)
            footnote1_r_splited_by_spaces = footnote1_r_in_one_text_by_spaces.split(" ")
            possible_measures = [
                "kg",
                "g",
                "L",
                "ml",
                "ud.",
                "kg)",
                "g)",
                "L)",
                "ml)",
                "ud.)",
            ]
            measure = "failed_recollecting_measure"
            quantity_measure = 0

            for possible_measure in possible_measures:
                if possible_measure in footnote1_r_splited_by_spaces:
                    measure = possible_measure
                    if ")" in measure:
                        measure = measure.replace(")", "")
                    break

            # Searching quantity in the left value of the measure
            for i in range(len(footnote1_r_splited_by_spaces)):
                if footnote1_r_splited_by_spaces[i] in possible_measures:
                    quantity_measure = footnote1_r_splited_by_spaces[i - 1]
                    if "(" in quantity_measure:
                        quantity_measure = quantity_measure.replace("(", "")
                    break

            price_per_measure = 0.0
            if quantity_measure != 0:
                price_per_measure = self.calculate_price_per_measure(
                    quantity_measure, measure, price
                )

            print(footnote1_r_splited_by_spaces)
            print(price_per_measure)
            print(quantity_measure)

            image_wrapper = product_html.find(
                "div", class_="product-cell__image-wrapper"
            )
            image_url = (
                image_wrapper.find("img")["src"] if image_wrapper else "[no-image]"
            )

            only_numbers_price = re.sub(r"[^\d.,]", "", price)
            only_numbers_price = only_numbers_price.replace(",", ".")
            price_float = float(only_numbers_price)

            store_name = "Mercadona"
            store_image_url = "https://mirasol-centre.com/nousite/wp-content/uploads/2017/05/logo-Mercadona.png"
            product = Product(
                str(uuid.uuid4()),
                id_category,
                name,
                price_float,
                int(quantity_measure),
                measure,
                price_per_measure,
                image_url,
                store_name,
                store_image_url,
            )
        except AttributeError as e:
            print(f"Error al obtener datos del producto: {e}")
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
        quantity = int(quantity)
        splited_euro = price_per_measure.split(" ")
        price_per_measure = float(splited_euro[0].replace(",", "."))

        if measure == "g":
            quantity_in_kg = quantity / 1000
            price_per_kg = price_per_measure / quantity_in_kg
            return price_per_kg
        elif measure == "kg":
            price_per_kg = price_per_measure / quantity
            return price_per_kg
        elif measure == "ml":
            quantity_in_liter = quantity / 1000
            price_per_liter = price_per_measure / quantity_in_liter
            return price_per_liter
        elif measure == "l":
            price_per_liter = price_per_measure / quantity
            return price_per_liter
        elif measure == "ud." and name_product.lower().contains("huevo"):
            # In Mercadona ud. is same as dozens eggs.
            quantity_in_dozen = quantity / 12
            return price_per_measure / quantity_in_dozen
        elif measure == "ud.":
            # Other cases with ud., it going to calculate with standard ud. as 1
            return price_per_measure / quantity
        else:
            return "Measure is not valid"

    def write_products_to_file(products, output_file):
        with open(output_file, "w", encoding="utf-8") as file:
            for product in products:
                file.write(str(product) + "\n")
