from api_wewiza.src.repositories.product_likes_repository import ProductLikesRepository
import json
import requests


class ProductLikesService:
    def __init__(self, product_likes_repository: ProductLikesRepository):
        self.product_likes_repository = product_likes_repository

    def calculate_like_average(self):
        result = self.product_likes_repository.get_all_products()

        if result.is_failure():
            print("Failed to get all products:", result.error)
            return 0

        products_list_json = result.value
        likes_sum = sum(product["num_likes"] for product in products_list_json)
        likes_count = len(products_list_json)
        average = likes_sum / likes_count
        print("AVERAGE_LIKES not rounded: ", average)
        average = round(average, 2)
        return average

    def get_top_products(self, TOP_LIKES_AVERAGE):
        query = {"num_likes": {"$gt": TOP_LIKES_AVERAGE}}
        result = self.product_likes_repository.get_all_products_by_query(query)

        if result.is_failure():
            print("Failed to get top categories:", result.error)
            return []

        products_list_json = result.value
        sorted_products = sorted(
            products_list_json, key=lambda x: x["num_likes"], reverse=True
        )
        top_5_products = sorted_products[:5]
        uuid_list = [product["uuid"] for product in top_5_products]

        return uuid_list

    def like_product(self, product_id, email_user):
        uuid_query = {"uuid": product_id}
        product_data = self.product_likes_repository.get_product_by_query(
            uuid_query
        ).value

        if not product_data:
            print("PRODUCT_LIKES_SERVICE: (Like) Product not found")
            return False

        if email_user in product_data.get("likes_email", []):
            print(
                "PRODUCT_LIKES_SERVICE: (Like) Product was liked before by "
                + email_user
            )
            return False

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
        return True

    def unlike_product(self, product_id, email_user):
        uuid_query = {"uuid": product_id}
        product_data = self.product_likes_repository.get_product_by_query(
            uuid_query
        ).value

        if not product_data:
            print("PRODUCT_LIKES_SERVICE: (Unlike) Product not found")
            return False

        if email_user in product_data.get("unlikes_email", []):
            print(
                "PRODUCT_LIKES_SERVICE: (Unlike) Product was unliked before by "
                + email_user
            )
            return False

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
        return True

    def get_reaction(self, email_user, product_id):
        uuid_query = {"uuid": product_id}
        product_data = self.product_likes_repository.get_product_by_query(
            uuid_query
        ).value

        if not product_data:
            return "Product not found"

        if email_user in product_data.get("likes_email", []):
            return "liked"

        if email_user in product_data.get("unlikes_email", []):
            return "unliked"

        return "none"

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
        if isinstance(product_json, dict):
            uuid = product_json["uuid"]
            product_data = self.product_likes_repository.get_product_by_query(
                {"uuid": uuid}
            ).value
            if product_data:
                product_json["num_likes"] = product_data["num_likes"]
                return product_json

        return product_json

    def map_products_json_list(self, products_json_list):
        products_json_list = [item for item in products_json_list if item is not None]
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
