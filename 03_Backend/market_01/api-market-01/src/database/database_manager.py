from pymongo import MongoClient


class DatabaseManager:
    def __init__(self, connection_host, database_name):
        self.connection_host = connection_host
        self.database_name = database_name
        self.client = None
        self.database = None

    def __create_database(self):
        if self.database_name not in self.client.list_database_names():
            self.client[self.database_name]

    def create_collection_with_validation(self, collection_name, new_validator=None):
        """
        Only is going to be executed in the repositories when instantiating
        """
        if collection_name not in self.database.list_collection_names():
            self.database.create_collection(collection_name, validator=new_validator)

    def connect_database(self):
        self.client = MongoClient(self.connection_host)
        self.database = self.client[self.database_name]
        self.__create_database()
        return self.database

    def close_database(self):
        if self.client:
            self.client.close()
