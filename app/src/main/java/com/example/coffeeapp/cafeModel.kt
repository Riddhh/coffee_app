package com.example.coffeeapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Coffee(
    val _id: String? = null,
    val name: String,
    val image: String,       // your MongoDB image URL
    val price: Double,
    val category: String,
    val description: String
) : Parcelable
