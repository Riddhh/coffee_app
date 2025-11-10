package com.example.coffeeapp.model

data class User(
    val id: String? = null,            // Assigned by MongoDB backend
    val email: String = "",
    val passwordHash: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
