import os
import requests
from bs4 import BeautifulSoup
from database.DatabaseManager import DatabaseManager
from models.Product import Product
from repositories.ProductRepository import ProductRepository
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException

DATABASE_NAME = "eroski"
COLLECTION = "products"

class SimulationEroski:
    def __init__(self, driver, connection_mongo):
        self.driver = driver
        self.connection_mongo = connection_mongo
        database_manager = DatabaseManager(self.connection_mongo, DATABASE_NAME)
        self.product_repository =  ProductRepository(database_manager, COLLECTION)
        self.url_main = "https://supermercado.eroski.es:443"

    def scrappe_main_categorias(self):
        
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'}
        response = requests.get(self.url_main, headers=headers)

        if response.status_code == 200:
            self.driver.get(self.url_main)
            self.driver.implicitly_wait(20)

            soup = BeautifulSoup(response.text, 'html.parser')
            categorias = []

            # Encuentra la lista de categorías
            categorias_lista = soup.find('ul', {'data-view-section': 'Ver todos'})
            
            if categorias_lista:
                # Itera sobre cada elemento de la lista de categorías
                for categoria_element in categorias_lista.find_all('a', class_="not_clickable", href=True):
                
                    categoria_url = categoria_element["href"]

                    # Agrega la categoría a la lista
                    categorias.append(categoria_url)

            return categorias

        return None
    
    def scrappe_sub_categories(self, list_main_urls_categories):
      for url_main_category in list_main_urls_categories:
            response = requests.get(url_main_category)

            if response.status_code == 200:
                self.driver.get(url_main_category)
                self.driver.implicitly_wait(20)

                soup = BeautifulSoup(response.text, 'html.parser')
                sub_categorias = []

                sub_categories_list = soup.find('ul', class_='m__list_category__list')
                
                for sub_categoria_url in sub_categories_list.find_all('a', href=True):
                    sub_categorias.append(sub_categoria_url["href"])
                
                return sub_categorias

    def run_simulation(self, sub_categories_list):
      output_folder = 'data'
      if not os.path.exists(output_folder):
        os.makedirs(output_folder)

      for sub_categorie_url in sub_categories_list:
        sub_categori_url_concatenate = f'{self.url_main}{sub_categorie_url}'

        response = requests.get(sub_categori_url_concatenate)

        if response.status_code == 200:
            self.driver.get(sub_categori_url_concatenate)
            self.driver.implicitly_wait(20)

            soup = BeautifulSoup(response.text, 'html.parser')

            products_list = soup.find('div', id="productListZone")

            print(len(products_list.find_all('h2')))
                  
      self.driver.quit()

      def run_(self, list_main_urls_categories):
          for i in range(start_category, end_category + 1):

            # Verificar si la página existe antes de intentar acceder a ella
            response = requests.get(url_main_category)
            if response.status_code == 200:
              print(f"Accediendo a la página: {url_main_category}")

              output_file = f'output{i}.txt'
              url_main_category = f'https://tienda.mercadona.es/categories/{i}'
              self.driver.get(url_main_category)

              try:
                # Esperar a que la página se cargue completamente
                self.driver.implicitly_wait(50)

                # Obtener el título de la página
                page_title_element = self.driver.find_element('css selector', '.category-detail__title.title1-b')
                page_title = page_title_element.text.strip()
                output_file = f'{output_folder}/output_{page_title}.txt'

                # Obtener el contenido de la página después de que JavaScript ha cargado los datos dinámicamente
                page_source = self.driver.page_source

                # Utilizar BeautifulSoup para analizar el HTML
                soup = BeautifulSoup(page_source, 'html.parser')

                # Buscar un patrón específico que indique un mensaje de error 404
                error_message = soup.find_all('div', class_='error-404')
                if error_message and '404' in error_message.text:
                  print(f"La página no existe: {url_main_category}")
                else:
                  # Encontrar los elementos HTML que contienen información sobre los productos
                  products_html = soup.find_all('div', class_='product-cell')

                  # Crear una lista para almacenar instancias de la clase Producto
                  products = []

                  # Iterar sobre los elementos y extraer información
                  for product_html in products_html:
                    product = process_product_html(product_html)
                    products.append(product)

                  # Imprimir la información de los productos
                  for product in products:
                    print(product)

                  # Escribir la información en un archivo
                  write_products_to_file(products, output_file)

                  # Insertar los datos en MongoDB
                  insert_products_to_mongodb(products)
              except Exception as e:
                print(f"Error al procesar la página {i}: {e}")
                output_file = f'{output_folder}/output{i}.txt'
                write_products_to_file(products, output_file)
            else:
              print(f"La página no existe: {url_main_category}")

          # Cerrar el navegador
          self.driver.quit()

      def process_product_html(product_html):
        try:
          name = product_html.find('h4', class_='subhead1-r product-cell__description-name').text
          price = product_html.find('p', class_='product-price__unit-price subhead1-b').text
          quantityPerPrice = product_html.find('p', class_='product-price__extra-price subhead1-r').text
          quantity = product_html.find('span', class_='footnote1-r').text
          # Obtener la URL de la imagen
          image_wrapper = product_html.find('div', class_='product-cell__image-wrapper')
          image = image_wrapper.find('img')['src'] if image_wrapper else "[no-image]"

          # Crear instancia de la clase Producto y agregar a la lista
          product = Product(name, price, quantityPerPrice, quantity, image)
        except AttributeError as e:
          print(f"Error al obtener datos del producto: {e}")
          no_data = "[no-data]"
          product = Product(no_data, no_data, no_data, no_data, image)

        return product

      def write_products_to_file(products, output_file):
        with open(output_file, 'w', encoding='utf-8') as file:
          for product in products:
            file.write(str(product) + '\n')

      def insert_products_to_mongodb(products):
        for product in products:
          product_data = {
            "name": product.name,
            "price": product.price,
            "quantityPerPrice": product.quantityPerPrice,
            "quantity": product.quantity,
            "image": product.image
          }
          try:
            self.product_repository.insert_product(product_data)
          except Exception as mongo_error:
            print(f"Error al insertar en MongoDB: {mongo_error}")