import json
import uuid
from selenium.webdriver.chrome.service import Service
from python_on_rails.result import Result
from bs4 import BeautifulSoup
from api_market_03.src.models.product import Product
from api_market_03.src.services.product_service import ProductService
import re
import os
import requests
from datetime import datetime


# TODO: change to carrefour
class ScrappingService:
    def __init__(self, driver, product_service: ProductService):
        self.driver = driver
        self.product_service = product_service
        self.output_folder = ""
        self.headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
        }

    def run_scrapping_carrefour(self):
        current_directory = os.path.dirname(os.path.realpath(__file__))

        api_market_03_folder = os.path.abspath(
            os.path.join(current_directory, "..", "..")
        )
        log_error_folder = os.path.join(api_market_03_folder, "log_error_carrefour")
        self.output_folder = log_error_folder

        # Only to keep a log when fail scrapping, because is imposible to check the failures in the console with a lot of products
        if not os.path.exists(log_error_folder):
            os.makedirs(log_error_folder)

        current_directory = os.path.dirname(os.path.realpath(__file__))
        static_folder = os.path.abspath(
            os.path.join(current_directory, "..", "..", "static")
        )
        categories_folder = os.path.join(
            static_folder, "carrefour_categories_urls_to_scrap"
        )

        categories_urls_dict = {}

        for category_id_wewiza in os.listdir(categories_folder):
            file_path = os.path.join(categories_folder, category_id_wewiza)

            if os.path.isfile(file_path):
                with open(file_path, "r") as f:
                    contenido = f.read()

                categories = contenido.split(";")
                category_id_wewiza = category_id_wewiza.replace(".csv", "")
                categories_urls_dict[category_id_wewiza] = categories

        self.scrap_categories(categories_urls_dict)

    # https://www.carrefour.es/supermercado/productos-frescos/carniceria/cat20018/c?offset=216
    # Va de 24 en 24, el offfset:
    #
    # Si no da Not found en sus ervidor renvia al cliente a: https://www.carrefour.es/supermercado
    # NUM PRODUCTS / 24 = NUM PAGES
    def scrap_categories(self, categories_dict):
        for category_id_wewiza, categories_list in categories_dict.items():
            for categorie_carrefour in categories_list:
                url = f"https://www.carrefour.es/supermercado/{categorie_carrefour}"

                response = requests.get(url, headers=self.headers)
                if response.status_code == 200:
                    print(f"Accessing to page: {url}")

                    output_file = f"output{categorie_carrefour}.txt"
                    self.driver.get(url)
                    counter_products_to_change_page = 0

                    try:
                        # Wait for the page to load
                        self.driver.implicitly_wait(20)

                        # Obtaining content page after JavaScript has loaded the data dynamically
                        page_source = self.driver.page_source
                        soup = BeautifulSoup(page_source, "html.parser")

                        # Find num pages
                        num_products = (
                            soup.find("div", class_="pagination__results")
                            .find_all("span", class_="pagination__results-item")[2]
                            .text
                        )

                        # SUM +24 until counter is > num_products
                        while counter_products_to_change_page < int(num_products):
                            url = f"https://www.carrefour.es/supermercado/{categorie_carrefour}?offset={counter_products_to_change_page}"
                            counter_products_to_change_page += 24

                            self.driver.get(url)
                            self.driver.implicitly_wait(20)
                            page_source = self.driver.page_source
                            soup = BeautifulSoup(page_source, "html.parser")

                            products_html = soup.find_all("div", class_="product-card")

                            for product_html in products_html:
                                product_model = self.map_product_html_to_model(
                                    product_html, category_id_wewiza
                                )

                                if product_model.name != "[no-data]":
                                    # TODO: change when need it
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
            "http://wewiza.ddns.net:83/insert_new_scrapped_product",
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
            url_details_product = (
                "https://www.carrefour.es"
                + product_html.find("a", class_="product-card__media-link track-click")[
                    "href"
                ]
            )
            self.driver.get(url_details_product)
            self.driver.implicitly_wait(20)
            page_source = self.driver.page_source
            soup = BeautifulSoup(page_source, "html.parser")

            name = soup.find("h1", class_="product-header__name").text.strip()
            print(name)

            price = soup.find("span", class_="buybox__price").text.strip()
            price_float = float(
                price.replace("€", "").replace(",", ".").replace(" ", "")
            )
            print(price_float)

            nutrition_more_info_container = soup.find(
                "div", class_="nutrition-more-info"
            )
            nutrition_info_box = nutrition_more_info_container.find(
                "div", class_="nutrition-more-info__box"
            )
            nutrition_more_info_inner_container_quantity_measure = (
                nutrition_info_box.find_all(
                    "div", class_="nutrition-more-info__container"
                )[1]
            )

            not_clear_quantity_measure = (
                nutrition_more_info_inner_container_quantity_measure.find(
                    "span", class_="nutrition-more-info__list-value"
                ).text
            ).strip()
            quantity_measure = float(not_clear_quantity_measure.split(" ")[0])
            print(quantity_measure)

            measure = str(not_clear_quantity_measure.split(" ")[1])
            print(measure)

            price_per_standard_measure_span_tag = soup.find(
                "div", class_="buybox__price-per-unit"
            ).span
            not_clear_price_per_standard_measure = (
                price_per_standard_measure_span_tag.get_text(strip=True)
            )
            price_per_standard_measure = not_clear_price_per_standard_measure.strip()
            price_per_standard_measure = float(
                price_per_standard_measure.split(" ")[0]
                .replace(",", ".")
                .replace(" ", "")
            )
            print(price_per_standard_measure)

            image_wrapper = soup.find("div", class_="main-image__container")
            image_url = (
                image_wrapper.find("img")["src"] if image_wrapper else "[no-image]"
            )

            store_name = "Carrefour"
            store_image_url = "https://imgs.search.brave.com/bUVy6bRXPYe0iSsDMPqwHb9Q67xTQaYOWcUND7ZOkdQ/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9hc3Nl/dHMuc3RpY2twbmcu/Y29tL2ltYWdlcy81/ODQyOTA2Y2E2NTE1/YjFlMGFkNzVhYmIu/cG5n"

            current_date = datetime.now()
            current_date_str = current_date.strftime("%Y-%m-%d %H:%M:%S")

            product = Product(
                str(uuid.uuid4()),
                id_category,
                name,
                price_float,
                float(quantity_measure),
                measure,
                price_per_standard_measure,
                image_url,
                url_details_product,
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

    def write_error_to_file(self, error, output_file):
        with open(output_file, "a", encoding="utf-8") as file:
            file.write(error + "\n")
