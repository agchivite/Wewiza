from python_on_rails.result import Result
from api_market_01.src.schemas.product_schema import product_schema
from api_market_01.src.database.database_manager import DatabaseManager
from datetime import datetime
import random
import re
import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from textblob import TextBlob

nltk.download("punkt")
nltk.download("stopwords")


class ProductRepository:
    def __init__(self, db_manager: DatabaseManager, collection_name):
        self.db_manager = db_manager
        self.collection_name = collection_name
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

            # Check if is scrapped in the same day
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

    def get_all_products_by_category_id(self, category_id):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            products = list(collection.find({"category_id": category_id}))
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
        blob = TextBlob(text.lower())
        stop_words = set(stopwords.words("spanish"))
        tokens = [word.lemmatize() for word in blob.words if word not in stop_words]
        return " ".join(tokens)

    def get_products_by_similar_name(self, product_name):
        try:

            normalized_name = self.__normalize_text(product_name)
            words = normalized_name.split()

            # Delete words that are not working
            words_not_working = ["hacendado"]

            # Chck workd in lowert case an trim text in both list
            # words = [word.lower().strip() for word in words if word not in words_not_working]

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

    def get_products_by_date(self, date):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            products = list(collection.find({"date_created": {"$regex": f"^{date}"}}))
            self.db_manager.close_database()
            return Result.success(products)
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

    def delete_products_by_date(self, date):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            result = collection.delete_many({"date_created": {"$regex": f"^{date}"}})
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

    def update_to_random_price_less(self):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]

            documents = collection.find(
                {"price_by_standard_measure": {"$exists": True}}
            )

            for doc in documents:
                price = doc["price_by_standard_measure"]
                rounded_price = round(price, 2)
                collection.update_one(
                    {"_id": doc["_id"]},
                    {"$set": {"price_by_standard_measure": rounded_price}},
                )

            self.db_manager.close_database()
            return Result.success("Ok")
        except Exception as e:
            return Result.failure(str(e))

    def updateZeroData(self):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]

            documents = collection.find({"price": 0})

            for doc in documents:
                price = random.uniform(0.31, 0.51)
                rounded_price = round(price, 2)
                collection.update_one(
                    {"_id": doc["_id"]},
                    {"$set": {"price": rounded_price}},
                )

            self.db_manager.close_database()
            return Result.success("Ok")
        except Exception as e:
            return Result.failure(str(e))

    def find_actual_id(self, uuid):
        try:
            database = self.db_manager.connect_database()
            collection = database[self.collection_name]
            product = collection.find_one({"uuid": uuid})

            if product:
                name_pasta_product = product["name"]
                actual_date_year_month = datetime.now().strftime("%Y-%m")
                product = collection.find_one(
                    {
                        "$and": [
                            {"name": name_pasta_product},
                            {"date_created": {"$regex": f"^{actual_date_year_month}"}},
                        ]
                    }
                )
            self.db_manager.close_database()
            return Result.success(product["uuid"])
        except Exception as e:
            return Result.failure(str(e))
