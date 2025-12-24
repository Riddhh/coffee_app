package com.example.coffeeapp.micro

import com.example.coffeeapp.Coffee
import retrofit2.http.GET
import retrofit2.http.Query

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

    @GET("products/search")
    suspend fun searchProducts(@Query("q") q: String): List<Coffee>
}
