from api_wewiza.src.repositories.product_likes_repository import ProductLikesRepository
import json


class ProductLikesService:
    def __init__(self, product_likes_repository: ProductLikesRepository):
        self.product_likes_repository = product_likes_repository

    def get_all_products(self):
        return self.product_likes_repository.get_all_products()

    def like_product(self, product_id):
        query = {"uuid": product_id}
        product_data = self.product_likes_repository.get_product_by_uuid(query).value
        if not product_data:
            return "Product not found"

        new_data = {"num_likes": product_data["num_likes"] + 1}
        self.product_likes_repository.update_product(query, new_data)
        return "Product liked"

    def unlike_product(self, product_id):
        query = {"uuid": product_id}
        product_data = self.product_likes_repository.get_product_by_uuid(query).value
        if not product_data:
            return "Product not found"

        new_data = {"num_likes": product_data["num_likes"] - 1}
        self.product_likes_repository.update_product(query, new_data)
        return "Product unliked"

    def insert_products_json_list(self, products_json_list):
        products_data = [
            {"uuid": product_json["uuid"], "num_likes": 0}
            for product_json in products_json_list
        ]

        self.product_likes_repository.insert_products_json(products_data).value

    def map_products_json_list(self, products_json_list):
        uuids = [product_json["uuid"] for product_json in products_json_list]

        products_data = self.product_likes_repository.get_products_by_uuids(uuids).value
        products_data_map = {product["uuid"]: product for product in products_data}

        for product_json in products_json_list:
            uuid = product_json["uuid"]
            product_data = products_data_map.get(uuid)
            if product_data:
                product_json["num_likes"] = product_data["num_likes"]

        return products_json_list
