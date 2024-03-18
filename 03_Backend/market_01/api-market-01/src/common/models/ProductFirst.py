class ProductFirst:
    def __init__(self, name, price, quantityPerPrice, quantity, image):
        self.name = name
        self.price = price
        self.quantityPerPrice = quantityPerPrice
        self.quantity = quantity
        self.image = image

    def __str__(self):
        return f"Product: {self.name} {self.price}  {self.quantity} {self.quantity}"
