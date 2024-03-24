from api_market_02.src.models.product import Product
from api_market_02.src.repositories.product_repository import ProductRepository
import json


class ProductService:
    def __init__(self, product_repository: ProductRepository):
        self.product_repository = product_repository

    def good_inyection(self):
        print("Ok, inyection")
        self.create_product()

    def create_product(self, product: Product):
        json_product = {
            "uuid": product.uuid,
            "category_uuid": product.category_uuid,
            "name": product.name,
            "price": product.price,
            "measure": product.measure,
            "price_by_measure": product.price_by_measure,
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
            print("Product inserted successfully with UUID:", result.value)
            # print(json.dumps(json_product, indent=4))
            # TODO: return success