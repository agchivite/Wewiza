from pymongo import MongoClient

class DatabaseManager:
    def __init__(self, connection_string, database_name):
        self.connection_string = connection_string
        self.database_name = database_name
        self.client = None
        self.database = None

    def connect_database(self):
        self.client = MongoClient(self.connection_string)
        self.database = self.client[self.database_name]
        return self.database

    def close_database(self):
        if self.client:
            self.client.close()

    def get_database(self):
        return self.database

    def get_connection_string(self):
        return self.connection_string

    def get_database_name(self):
        return self.database_name