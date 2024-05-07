from api_wewiza.src.repositories.product_likes_repository import ProductLikesRepository
import json


class ProductLikesService:
    def __init__(self, product_likes_repository: ProductLikesRepository):
        self.product_likes_repository = product_likes_repository

    def get_all_products(self):
        return self.product_likes_repository.get_all_products()

    def like_product(self, product_id, email_user):
        uuid_query = {"uuid": product_id}
        product_data = self.product_likes_repository.get_product_by_query(
            uuid_query
        ).value

        if not product_data:
            return "Product not found"

        if email_user in product_data.get("likes_email", []):
            return "Product was liked before by " + email_user

        new_num_likes = product_data.get("num_likes", 0) + 1
        likes_emails = product_data.get("likes_email", [])
        likes_emails.append(email_user)

        update_query = {"uuid": product_id}
        update_data = {
            "$set": {"num_likes": new_num_likes, "likes_email": likes_emails}
        }

        # Remove email from unlikes_email if it was there
        if email_user in product_data.get("unlikes_email", []):
            update_data["$pull"] = {"unlikes_email": email_user}
            # Sum one more like because it was unliked before
            update_data["$set"]["num_likes"] = new_num_likes + 1

        self.product_likes_repository.update_product(update_query, update_data)
        return "Product liked"

    def unlike_product(self, product_id, email_user):
        uuid_query = {"uuid": product_id}
        product_data = self.product_likes_repository.get_product_by_query(
            uuid_query
        ).value

        if not product_data:
            return "Product not found"

        if email_user in product_data.get("unlikes_email", []):
            return "Product was unliked before by " + email_user

        new_num_likes = product_data.get("num_likes", 0) - 1
        unlikes_emails = product_data.get("unlikes_email", [])
        unlikes_emails.append(email_user)

        update_query = {"uuid": product_id}
        update_data = {
            "$set": {"num_likes": new_num_likes, "unlikes_email": unlikes_emails}
        }

        # Remove email from likes_email if it was there
        if email_user in product_data.get("likes_email", []):
            update_data["$pull"] = {"likes_email": email_user}
            # Subtract one like because it was liked before
            update_data["$set"]["num_likes"] = new_num_likes - 1

        self.product_likes_repository.update_product(update_query, update_data)
        return "Product unliked"

    def insert_products_json_list(self, products_json_list):
        products_data = [
            {
                "uuid": str(product_json["uuid"]),
                "num_likes": 0,
                "likes_email": [],
                "unlikes_email": [],
                "date_created": str(product_json["date_created"]),
            }
            for product_json in products_json_list
        ]

        self.product_likes_repository.insert_products_json(products_data)

    def map_product_json(self, product_json):
        uuid = product_json["uuid"]
        product_data = self.product_likes_repository.get_product_by_query(
            {"uuid": uuid}
        ).value
        if product_data:
            product_json["num_likes"] = product_data["num_likes"]

        return product_json

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

    def delete_all_products_by_actual_month(self):
        return self.product_likes_repository.delete_all_products_by_actual_month()
