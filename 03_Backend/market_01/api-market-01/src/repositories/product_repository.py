class ProductRepository:
    def __init__(self, db_manager, collection_name):
        self.db_manager = db_manager
        self.collection_name = collection_name

    def insert_product(self, product_data):
        database = self.db_manager.connect_database()
        collection = database[self.collection_name]
        result = collection.insert_one(product_data)
        self.db_manager.close_database()
        return result.inserted_id

    def get_all_products(self):
        database = self.db_manager.connect_database()
        collection = database[self.collection_name]
        products = list(collection.find())
        self.db_manager.close_database()
        return products

    def update_product(self, query, new_data):
        database = self.db_manager.connect_database()
        collection = database[self.collection_name]
        result = collection.update_one(query, {"$set": new_data})
        self.db_manager.close_database()
        return result.modified_count

    def delete_product(self, query):
        database = self.db_manager.connect_database()
        collection = database[self.collection_name]
        result = collection.delete_one(query)
        self.db_manager.close_database()
        return result.deleted_count
