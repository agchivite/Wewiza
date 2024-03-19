from ast import List
from src.models.product import Product


class Category:
    def __init__(self, id: str, name: str, products: List[Product]):
        self.id = id
        self.name = name
        self.products = products

    def __str__(self):
        return f"Category: {self.name}"
