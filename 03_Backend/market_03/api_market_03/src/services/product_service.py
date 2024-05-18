from api_market_03.src.models.product import Product
from api_market_03.src.repositories.product_repository import ProductRepository
import json
import datetime


class ProductService:
    def __init__(self, product_repository: ProductRepository):
        self.product_repository = product_repository

    ##################### TODO: CLASS UTILS... #####################
    def parse_date(self, date_str):
        return datetime.datetime.strptime(date_str, "%Y-%m-%d %H:%M:%S")

    #####################----------------#####################

    def update_all_date(self):
        result = self.product_repository.update_all_date()

        if result.is_failure():
            print("Failed to update all products:", result.error)
            return []

        return result.value

    def get_products_with_good_profit(self):
        actual_month = datetime.datetime.now().strftime("%Y-%m")
        result_actual_products = self.product_repository.get_products_by_date(
            actual_month
        )

        if result_actual_products.is_failure():
            print(
                "[ProductService] (Good profit) Failed to get products actual month:",
                result_actual_products.error,
            )
            return []

        last_month = (datetime.datetime.now() - datetime.timedelta(days=30)).strftime(
            "%Y-%m"
        )
        result_last_products = self.product_repository.get_products_by_date(last_month)

        if result_last_products.is_failure():
            print(
                "[ProductService] (Good profit) Failed to get products last month:",
                result_last_products.error,
            )
            return []

        ############### Get the difference  key ["price_by_standard_measure"] and calculate the profit ##################
        last_products_list_json = result_last_products.value
        actual_products_list_json = result_actual_products.value

        for last_product in last_products_list_json:
            for actual_product in actual_products_list_json:
                if (
                    last_product["uuid"] == actual_product["uuid"]
                    and last_product["price_by_standard_measure"]
                    != actual_product["price_by_standard_measure"]
                ):
                    actual_product["profit"] = (
                        last_product["price_by_standard_measure"]
                        - actual_product["price_by_standard_measure"]
                    )
                    actual_product["profit_percentage"] = (
                        actual_product["profit"]
                        / last_product["price_by_standard_measure"]
                    ) * 100

        # Only get the actual products that has key profit
        actual_products_list_json = [
            product for product in actual_products_list_json if "profit" in product
        ]

        # Removing _id key, we don't want it
        for product in actual_products_list_json:
            del product["_id"]

        return actual_products_list_json

    def get_size(self):
        result = self.product_repository.get_size()

        if result.is_failure():
            print("Failed to get size:", result.error)
            return []

        return result.value

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

    def get_all_products_by_market(self, market_name):
        result = self.product_repository.get_all_products_by_market(market_name)

        if result.is_failure():
            print("Failed to get all products by market name:", result.error)
            return []

        products_list_json = result.value

        # Removing _id key, we don't want it
        for product in products_list_json:
            del product["_id"]

        return products_list_json

    def get_products_by_range(self, init_num, end_num):
        result = self.product_repository.get_products_by_range(init_num, end_num)

        if result.is_failure():
            print("Failed to get products by range:", result.error)
            return []

        products_list_json = result.value

        # Removing _id key, we don't want it
        for product in products_list_json:
            del product["_id"]

        return products_list_json

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

        if product_json is None:
            return result.value

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
