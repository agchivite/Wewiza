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
                            product_model = self.map_product_html_to_model(product_html)
                            self.product_service.create_product(product_model)
                            print(product_model)
                except Exception as e:
                    print(f"Error al procesar la página {i}: {e}")
                    # output_file = f"{output_folder}/output{i}.txt"
                    # write_products_to_file(products, output_file)
            else:
                print(f"La página no existe: {url}")

        self.driver.quit()

    # TODO: make a mapper DTO
    def map_product_html_to_model(self, product_html):
        try:
            name = product_html.find(
                "h4", class_="subhead1-r product-cell__description-name"
            ).text
            price = product_html.find(
                "p", class_="product-price__unit-price subhead1-b"
            ).text
            quantityPerPrice = product_html.find(
                "p", class_="product-price__extra-price subhead1-r"
            ).text
            quantity = product_html.find("span", class_="footnote1-r").text
            image_wrapper = product_html.find(
                "div", class_="product-cell__image-wrapper"
            )
            image = image_wrapper.find("img")["src"] if image_wrapper else "[no-image]"

            only_numbers_price = re.sub(r"[^\d.,]", "", price)
            only_numbers_price = only_numbers_price.replace(",", ".")
            price_float = float(only_numbers_price)

            only_numbers_price_by_measure = re.sub(r"[^\d.,]", "", price)
            only_numbers_price_by_measure = only_numbers_price_by_measure.replace(
                ",", "."
            )
            price_float_price_by_measure = float(only_numbers_price_by_measure)

            # TODO: change to real product and follow the sequence of the program, to check possible errors
            product = Product(
                name,
                name,
                name,
                name,
                price_float,
                name,
                price_float_price_by_measure,
                name,
                name,
                name,
            )
        except AttributeError as e:
            print(f"Error al obtener datos del producto: {e}")
            no_data = "[no-data]"
            # TODO: change to real product and follow the sequence of the program, to check possible errors
            product = Product(
                no_data,
                no_data,
                no_data,
                no_data,
                0.0,
                no_data,
                0.0,
                no_data,
                no_data,
                no_data,
            )

        return product

    def write_products_to_file(products, output_file):
        with open(output_file, "w", encoding="utf-8") as file:
            for product in products:
                file.write(str(product) + "\n")
