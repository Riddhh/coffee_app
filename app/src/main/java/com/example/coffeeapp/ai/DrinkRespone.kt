package com.example.coffeeapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DrinkResponse(
    val name: String,

    val description: String = "",

    // Support multiple possible keys from your API/DB:
    @SerialName("image")
    val image: String? = null,

    @SerialName("imageUrl")
    val imageUrl: String? = null,

    @SerialName("img")
    val img: String? = null,

    val price: Double = 0.0
) {
    // Use this in UI (popup). Picks first non-null URL.
    val photo: String?
        get() = image ?: imageUrl ?: img
}