from api_market_01.src.models.product import Product
from api_market_01.src.repositories.product_repository import ProductRepository
import json


class ProductService:
    def __init__(self, product_repository: ProductRepository):
        self.product_repository = product_repository

    def get_all_products(self):
        result = self.product_repository.get_all_products()

        if result.is_failure():
            print("Failed to get all products:", result.error)
            return []

        products_list_json = result.value

        # Removing _id key, we don't want it
        for product in products_list_json:
            del product["_id"]

        return products_list_json

    def create_product_to_mongo_recieving_json(self, product_json: str):
        product_dict = json.loads(product_json)

        result = self.product_repository.insert_product(product_dict)
        if result.is_failure():
            return result.error
        else:
            return result.value

    def get_all_products_by_category_id(self, category_id):
        result = self.product_repository.get_all_products_by_category_id(category_id)

        if result.is_failure():
            print("Failed to get all products by category id:", result.error)
            return []

        products_list_json = result.value

        # Removing _id key, we don't want it
        for product in products_list_json:
            del product["_id"]

        return products_list_json

    def get_product_by_uuid(self, uuid):
        result = self.product_repository.get_product_by_uuid(uuid)

        if result.is_failure():
            print("Failed to get product by uuid:", result.error)
            return []

        product_json = result.value

        # Removing _id key, we don't want it
        del product_json["_id"]

        return product_json

    def get_products_by_name(self, product_name):
        result = self.product_repository.get_products_by_name(product_name)

        if result.is_failure():
            print("Failed to get products by name:", result.error)
            return []

        products_list_json = result.value

        # Removing _id key, we don't want it
        for product in products_list_json:
            del product["_id"]

        return products_list_json
