class Product:
    def __init__(
        self,
        uuid: str,
        category_id: str,
        name: str,
        price: float,
        quantity_measure: float,
        measure: str,
        price_by_standard_measure: float,
        image_url: str,
        store_name: str,
        store_image_url: str,
    ):
        self.uuid = uuid
        self.category_id = category_id
        self.name = name
        self.price = price
        self.quantity_measure = quantity_measure
        self.measure = measure
        self.price_by_standard_measure = price_by_standard_measure
        self.image_url = image_url
        self.store_name = store_name
        self.store_image_url = store_image_url

    def dict(self):
        return {
            "uuid": self.uuid,
            "category_id": self.category_id,
            "name": self.name,
            "price": self.price,
            "quantity_measure": self.quantity_measure,
            "measure": self.measure,
            "price_by_standard_measure": self.price_by_standard_measure,
            "image_url": self.image_url,
            "store_name": self.store_name,
            "store_image_url": self.store_image_url,
        }

    def __str__(self):
        return f"Product: {self.name}"
