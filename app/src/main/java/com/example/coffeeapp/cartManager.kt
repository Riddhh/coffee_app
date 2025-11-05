package com.example.coffeeapp

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.android.parcel.Parcelize
import kotlin.time.times
@Parcelize
data class cartItem(
    val name: String,
    val img: String,
    val price: Double,
    val size: String,
//    var quantity:MutableState<Int> = mutableStateOf(1)
    var quantity:  Int = 1
): Parcelable
object cartManager{
    val items = mutableStateListOf<cartItem>()
    fun addItem(item: cartItem) {
        // ✅ Check if item with same name and size already exists
        val existing = items.find { it.name == item.name && it.size == item.size }
        if (existing != null) {
            existing.quantity += item.quantity // increase quantity
        } else {
            items.add(item)
        }
    }

    fun removeItem(item: cartItem) {
        items.remove(item)
    }

    fun clearCart() {
        items.clear()
    }
    fun totalPrice(): Double {
        return items.sumOf { it.price * it.quantity }
    }

}