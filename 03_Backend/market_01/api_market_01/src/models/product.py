class Product:
    def __init__(
        self,
        uuid: str,
        category_uuid: str,
        name: str,
        price: float,
        quantity_measure: int,
        measure: str,
        price_by_standard_measure: float,
        image_url: str,
        store_name: str,
        store_image_url: str,
    ):
        self.uuid = uuid
        self.category_uuid = category_uuid
        self.name = name
        self.price = price
        self.quantity_measure = quantity_measure
        self.measure = measure
        self.price_by_standard_measure = price_by_standard_measure
        self.image_url = image_url
        self.store_name = store_name
        self.store_image_url = store_image_url

    def __str__(self):
        return f"Product: {self.name}"
