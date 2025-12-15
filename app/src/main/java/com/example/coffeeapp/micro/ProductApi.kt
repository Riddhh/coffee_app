package com.example.coffeeapp.micro

import retrofit2.http.GET

data class Product(
    val _id: String? = null,
    val name: String,
    val price: Double,
    val description: String? = null,
    val size: String? = null,
    val imageUrl: String? = null
)

interface ProductApi {
    @GET("products")
    suspend fun getProducts(): List<Product>
}
