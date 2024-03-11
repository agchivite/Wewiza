package dev.sbytmacke.firstcompose.data.repositories

import dev.sbytmacke.firstcompose.models.Product

class ProductRepository {

    private val products: List<Product> = listOf(Product(0, "Product 1", 1))

    fun getProduct(id: Int): Product {
        return products.first { it.id == id }
    }

    fun getProducts(): List<Product> {
        return products
    }
}
