import json
import uuid
from selenium.webdriver.chrome.service import Service
from bs4 import BeautifulSoup
from api_market_01.src.models.product import Product
from api_market_01.src.services.product_service import ProductService
import re
import os
import requests
from datetime import datetime


class ScrappingService:
    def __init__(self, driver, product_service: ProductService):
        self.driver = driver
        self.product_service = product_service
        self.output_folder = ""

    def find_category(self, category_title, dictionaty_titles_categories):
        for category, subcategories in dictionaty_titles_categories.items():
            if any(
                category_title.lower() in subcategory.lower()
                for subcategory in subcategories
            ):
                return category
        return "category_not_found"

    def run_scrapping_mercadona(
        self, list_num_categories, dictionaty_titles_categories
    ):
        current_directory = os.path.dirname(os.path.realpath(__file__))

        api_market_02_folder = os.path.abspath(
            os.path.join(current_directory, "..", "..")
        )
        log_error_folder = os.path.join(api_market_02_folder, "log_error_mercadona")
        self.output_folder = log_error_folder

        # Only to keep a log when fail scrapping, because is imposible to check the failures in the console with a lot of products
        if not os.path.exists(log_error_folder):
            os.makedirs(log_error_folder)

        for i in list_num_categories:
            url = f"https://tienda.mercadona.es/categories/{i}"

            response = requests.get(url)
            if response.status_code == 200:
                print(f"Accessing to page: {url}")

                output_file = f"output{i}.txt"
                url = f"https://tienda.mercadona.es/categories/{i}"
                self.driver.get(url)

                try:
                    # Wait for the page to load
                    self.driver.implicitly_wait(20)

                    category_title_element = self.driver.find_element(
                        "css selector", ".category-detail__title.title1-b"
                    )
                    category_title = self.find_category(
                        category_title_element.text.strip(),
                        dictionaty_titles_categories,
                    )
                    id_category = self.map_category_title_to_id(category_title)

                    # Obtaining content page after JavaScript has loaded the data dynamically
                    page_source = self.driver.page_source
                    soup = BeautifulSoup(page_source, "html.parser")

                    # Searching for a specific pattern that matches the 404 error message, to not search constantly
                    error_message = soup.find_all("div", class_="error-404")
                    if error_message and "404" in error_message.text:
                        print(f"Page not found: {url}")
                    else:
                        products_html = soup.find_all("div", class_="product-cell")

                        for product_html in products_html:
                            product_model = self.map_product_html_to_model(
                                product_html, id_category, url
                            )

                            if product_model.name != "[no-data]":
                                # TODO: change when need it
                                self.send_to_wewiza_server(product_model, id_category)
                                # self.send_to_localhost_mongo(product_model)

                except Exception as e:
                    print(f"Error processing page {i}: {e}")
                    output_file = f"{self.output_folder}/output_category_{i}.txt"
                    self.write_error_to_file(
                        f"Error processing page {i}: {e}", output_file
                    )
            else:
                print(f"Page not found: {url}")

        self.driver.quit()

    def send_to_localhost_mongo(self, product_model):
        product_dict = product_model.dict()
        json_string = json.dumps(product_dict, indent=4)
        self.product_service.create_product_to_mongo_recieving_json(json_string)

    def send_to_wewiza_server(self, product_model: Product, id_category):
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
            output_file = f"{self.output_folder}/output_{id_category}.txt"
            self.write_error_to_file(
                f"Error inserting product to wewiza-server: {response.text}",
                output_file,
            )

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
    def map_product_html_to_model(self, product_html, id_category, url):
        try:
            name = product_html.find(
                "h4", class_="subhead1-r product-cell__description-name"
            ).text

            # Searching first for discount product
            price = 0.0
            discount_price = product_html.find(
                "p",
                class_="product-price__unit-price subhead1-b product-price__unit-price--discount",
            )

            if discount_price:
                price = discount_price.text
            else:
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
            quantity_measure = 0.0

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

            quantity_measure = float(quantity_measure.replace(",", "."))

            price_per_measure = 0.0
            if quantity_measure != 0.0:
                price_per_measure = self.calculate_price_per_measure(
                    quantity_measure, measure, price, name
                )

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

            current_date = datetime.now()
            current_date_str = current_date.strftime("%Y-%m-%d %H:%M:%S")

            product = Product(
                str(uuid.uuid4()),
                id_category,
                name,
                price_float,
                float(quantity_measure),
                measure.lower(),
                price_per_measure,
                image_url,
                url,
                store_name,
                store_image_url,
                current_date_str,
            )
        except AttributeError as e:
            print(f"Error obtaining data from product: {e}")
            output_file = f"{self.output_folder}/output_{id_category}.txt"
            self.write_error_to_file(
                f"Error obtaining data from product: {e}", output_file
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

    def calculate_price_per_measure(
        self, quantity, measure, price_per_measure, name_product
    ):
        splited_euro = price_per_measure.split(" ")
        price_per_measure = float(splited_euro[0].replace(",", "."))

        measure = measure.lower()

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
        elif (
            measure == "ud." and "huevo" in name_product.lower()
        ):  # In Mercadona ud. is same as dozens eggs.
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
