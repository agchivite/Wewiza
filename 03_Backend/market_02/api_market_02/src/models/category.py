from ast import List
from api_market_02.src.models.product import Product


class Category:
    def __init__(self, uuid: str, name: str, products: List[Product]):
        self.uuid = uuid
        self.name = name
        self.products = products

    def __str__(self):
        return f"Category: {self.name}"
