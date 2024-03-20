from src.models.product import Product
from src.repositories.product_repository import ProductRepository
import json
import re


class ProductService:
    def __init__(self, product_repository: ProductRepository):
        self.product_repository = product_repository

    def good_inyection(self):
        print("Ok, inyection")
        self.create_product()

    def create_product(self, product: Product):
        only_numbers_price = re.sub(r"[^\d.,]", "", product.price)
        only_numbers_price = only_numbers_price.replace(",", ".")
        price_float = float(only_numbers_price)

        only_numbers_price_by_measure = re.sub(r"[^\d.,]", "", product.price_by_measure)
        only_numbers_price_by_measure = only_numbers_price_by_measure.replace(",", ".")
        price_float_price_by_measure = float(only_numbers_price_by_measure)

        json_product = {
            "id": product.id,
            "category_id": product.category_id,
            "name": product.name,
            "currency": product.currency,
            "price": price_float,
            "measure": product.measure,
            "price_by_measure": price_float_price_by_measure,
            "image_url": product.image_url,
            "store_name": product.store_name,
            "store_image_url": product.store_image_url,
        }

        result = self.product_repository.insert_product(json_product)
        if result.is_failure():
            print("Failed to insert product:", result.error)
            # print(json.dumps(json_product, indent=4))
            # TODO: return failure
        else:
            print("Product inserted successfully with ID:", result.value)
            # print(json.dumps(json_product, indent=4))
            # TODO: return success
