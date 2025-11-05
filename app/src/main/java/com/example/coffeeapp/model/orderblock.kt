package com.example.coffeeapp.model

data class Order(
    val id: String,
    val timestamp: Long,
    val itemsSummary: String,
    val total: Double
)