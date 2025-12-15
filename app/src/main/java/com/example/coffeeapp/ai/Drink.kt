package com.example.coffeeapp

import kotlinx.serialization.Serializable

@Serializable
data class Drink(
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String
)