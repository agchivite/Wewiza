class Product:
    def __init__(
        self,
        id: str,
        category_id: str,
        name: str,
        price: float,
        measure: str,
        price_by_measure: float,
        image_url: str,
        store_name: str,
        store_image_url: str,
    ):
        self.name = name
        self.id = id
        self.category_id = category_id
        self.price = price
        self.measure = measure
        self.price_by_measure = price_by_measure
        self.image_url = image_url
        self.store_name = store_name
        self.store_image_url = store_image_url

    def __str__(self):
        return f"Product: {self.name}"
