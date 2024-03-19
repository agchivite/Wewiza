from src.database.database_manager import DatabaseManager
from src.models.product import Product
from src.models.product_first import ProductFirst

from src.repositories.product_repository import ProductRepository
import json
import re


class ProductService:
    def __init__(self, product_repository: ProductRepository):
        self.product_repository = product_repository

    def good_inyection(self):
        print("Ok, inyection")
        self.create_product()

    def create_product(self, product: ProductFirst):
        only_numbers = re.sub(r"[^\d.,]", "", product.price)
        only_numbers = only_numbers.replace(",", ".")
        price_float = float(only_numbers)

        json_product = {
            "id": product.name,
            "category_id": product.name,
            "name": product.name,
            "currency": product.name,
            "price": price_float,
            "measure": product.name,
            "price_by_measure": price_float,
            "image_url": product.name,
            "store_name": product.name,
            "store_image_url": product.name,
        }

        result = self.product_repository.insert_product(json_product)
        if result.is_failure():
            print("Failed to insert product:", result.error)
        else:
            print("Product inserted successfully with ID:", result.value)

        print("Pretty JSON good:")
        # print(json.dumps(json_product, indent=4))

        # return self.product_dao.create_product(product)

        """
        product_good = Product(
            "product_01",
            "catyegory_01",
            "patata",
            2.2,
            "kg",
            1.0,
            "store_01",
            "store_01",
            "store_01_img",
        )
        json_good = {
            "id": product_good.id,
            "category_id": product_good.category_id,
            "name": product_good.name,
            "price": product_good.price,
            "measure": product_good.measure,
            "price_by_measure": product_good.price_by_measure,
            "image_url": product_good.image_url,
            "store_name": product_good.store_name,
            "store_image_url": product_good.store_image_url,
        }
        """
