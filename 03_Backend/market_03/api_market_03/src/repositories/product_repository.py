from python_on_rails.result import Result
from api_market_03.src.schemas.product_schema import product_schema
from api_market_03.src.database.database_manager import DatabaseManager
from datetime import datetime
import random
#import spacy
import re


class ProductRepository:
    def __init__(self, db_manager: DatabaseManager, collection_name):
        self.db_manager = db_manager
        self.collection_name = collection_name
        self.nlp_spanish = ""  # spacy.load("es_core_news_sm")
        self.__setup_collection_validation()

    def __setup_collection_validation(self):
        try:
            self.db_manager.connect_database()
            self.db_manager.create_collection_with_validation(
                self.collection_name, product_schema
            )
            self.db_manager.close_database()
            return Result.success()
        except Exception as e:
            return Result.failure(str(e))

    def insert_product(self, product_data):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]

            # Check if is scrapped in the same month
            # We can find in mongo compass with: { date_created: { $regex: /^2024-04-21/}}
            product_date_created = product_data["date_created"].split()[0]
            existing_product = collection.find_one(
                {
                    "$and": [
                        {"name": product_data["name"]},
                        {"date_created": {"$regex": f"^{product_date_created}"}},
                    ]
                }
            )

            if existing_product:
                return Result.failure(
                    "Product already exists: "
                    + product_data["name"]
                    + " and date: "
                    + str(product_data["date_created"])
                )

            result = collection.insert_one(product_data)
            # Because ObjectID needs to be converted to string
            inserted_id = str(result.inserted_id)
            self.db_manager.close_database()
            return Result.success(inserted_id)
        except Exception as e:
            return Result.failure(str(e))

    # TODO: Implement this method no HARDCODE dates
    def update_all_date(self):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]

            # Realizar la actualización
            result = collection.update_many(
                {"date_created": {"$regex": "^2024-05-01"}},
                {"$set": {"date_created": "2024-04-30 00:00:00"}},
            )

            self.db_manager.close_database()
            return Result.success(result.modified_count)
        except Exception as e:
            return Result.failure(str(e))

    def get_products_by_date(self, date):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            products = list(collection.find({"date_created": {"$regex": f"^{date}"}}))
            self.db_manager.close_database()
            return Result.success(products)
        except Exception as e:
            return Result.failure(str(e))

    def get_size(self):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            actual_date_year_month = datetime.now().strftime("%Y-%m")
            query = {"date_created": {"$regex": f"^{actual_date_year_month}"}}
            size = collection.count_documents(query)
            self.db_manager.close_database()
            return Result.success(size)
        except Exception as e:
            return Result.failure(str(e))

    def get_all_products_by_query(self, query):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            products = list(collection.find(query))
            self.db_manager.close_database()
            return Result.success(products)
        except Exception as e:
            return Result.failure(str(e))

    def get_all_products(self):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            products = list(collection.find())
            self.db_manager.close_database()
            return Result.success(products)
        except Exception as e:
            return Result.failure(str(e))

    def get_all_products_by_market(self, market_name):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            products = list(collection.find({"store_name": market_name}))
            self.db_manager.close_database()
            return Result.success(products)
        except Exception as e:
            return Result.failure(str(e))

    def get_products_by_range(self, init_num, end_num):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            products = list(collection.find().skip(init_num).limit(end_num - init_num))
            self.db_manager.close_database()
            return Result.success(products)
        except Exception as e:
            return Result.failure(str(e))

    def get_all_products_by_category_id(self, category_id):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            products = list(collection.find({"category_id": category_id}))
            self.db_manager.close_database()
            return Result.success(products)
        except Exception as e:
            return Result.failure(str(e))

    def get_product_by_uuid(self, uuid):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            product = collection.find_one({"uuid": uuid})
            self.db_manager.close_database()
            return Result.success(product)
        except Exception as e:
            return Result.failure(str(e))

    def get_products_by_name(self, product_name):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            products = list(collection.find({"name": product_name}))
            self.db_manager.close_database()
            return Result.success(products)
        except Exception as e:
            return Result.failure(str(e))

    def __normalize_text(self, text):
        doc = self.nlp_spanish(text.lower())
        tokens = [
            token.lemma_ for token in doc if not token.is_stop and not token.is_punct
        ]
        return " ".join(tokens)

    def get_products_by_similar_name(self, product_name):
        try:

            normalized_name = self.__normalize_text(product_name)
            words = normalized_name.split()

            regex_pattern = ".*(" + "|".join(re.escape(word) for word in words) + ").*"

            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            regex_products = list(
                collection.find({"name": {"$regex": regex_pattern, "$options": "i"}})
            )

            matching_products = []
            for product in regex_products:
                product_words = self.__normalize_text(product["name"]).split()
                common_words_count = sum(1 for word in product_words if word in words)
                if common_words_count >= len(words) / 2:
                    matching_products.append(product)

            self.db_manager.close_database()
            return Result.success(matching_products)
        except Exception as e:
            return Result.failure(str(e))

    def delete_product(self, query):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            result = collection.delete_one(query)
            self.db_manager.close_database()
            return Result.success(result.deleted_count)
        except Exception as e:
            return Result.failure(str(e))

    def update_product(self, query, new_data):
        """
        Probably this method, is not going to be used, because we introduce all products to test our API functionality
        """
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            result = collection.update_one(query, {"$set": new_data})
            self.db_manager.close_database()
            return Result.success(result.modified_count)
        except Exception as e:
            return Result.failure(str(e))

    def update_price_by_standard_measure(self):
        # Update all products that have huevo o huevos in their name to measure -> ud.
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            result = collection.update_many(
                {"name": {"$regex": "huevo|Huevo|huevos|Huevos"}},
                {"$set": {"measure": "ud."}},
            )
            self.db_manager.close_database()
            return Result.success(result.modified_count)
        except Exception as e:
            return Result.failure(str(e))

    def update_to_random_price_less(self):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]

            modified_count = 0
            actual_date_year_month = datetime.now().strftime("%Y-%m")
            documents = collection.find(
                {"date_created": {"$regex": f"^{actual_date_year_month}"}}
            )

            for doc in documents:
                current_price = doc.get("price", 0)
                if current_price > 0:
                    new_price = round(
                        max(current_price + random.uniform(0.3, 0.5), 0), 2
                    )
                    result = collection.update_one(
                        {"_id": doc["_id"]}, {"$set": {"price": new_price}}
                    )
                    modified_count += result.modified_count

            self.db_manager.close_database()
            return Result.success(modified_count)
        except Exception as e:
            return Result.failure(str(e))
