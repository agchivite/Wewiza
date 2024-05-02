from api_wewiza.src.repositories.product_likes_repository import ProductLikesRepository
import json


class ProductLikesService:
    def __init__(self, product_likes_repository: ProductLikesRepository):
        self.product_likes_repository = product_likes_repository

    def insert_products_json_list(self, products_json_list):
        for product_json in products_json_list:
            uuid = product_json["uuid"]
            num_likes: int = 0
            product_data = {"uuid": uuid, "num_likes": num_likes}
            print(self.product_likes_repository.insert_product_json(product_data).value)

    def map_products_json_list(self, products_json_list):
        for product_json in products_json_list:
            uuid = product_json["uuid"]
            product_data = self.product_likes_repository.get_product_by_uuid(uuid).value
            if product_data:
                product_json["num_likes"] = product_data["num_likes"]
        return products_json_list
