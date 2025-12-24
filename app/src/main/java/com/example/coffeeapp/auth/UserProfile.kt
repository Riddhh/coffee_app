package com.example.coffeeapp.auth

import com.squareup.moshi.Json

data class UserProfile(
    @Json(name = "_id")
    val id: String,

    val name: String? = null,
    val email: String
)
