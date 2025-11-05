package com.example.coffeeapp

import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object CoffeeRepository {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // ignores extra fields like _id
            })
        }
    }

    suspend fun fetchCoffees(): List<Coffee> {
        return client.get("http://10.0.2.2:3000/products").body()
    }
}
