from python_on_rails.result import Result
from api_market_01.src.schemas.product_schema import product_schema
from api_market_01.src.database.database_manager import DatabaseManager


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
            result = collection.insert_one(product_data)
            self.db_manager.close_database()
            return Result.success(result.inserted_id)
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

    def get_todos_by_user_email(self, user_email):
        database = self.db_manager.connect_database()
        collection = database[self.collection_name]
        filter = {"user_email": user_email}
        todos = list(collection.find(filter))
        # Convertimos ObjectId a string, si no Python no puede convertirlo
        for todo in todos:
            todo["_id"] = str(todo["_id"])
        self.db_manager.close_database()
        return todos

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
