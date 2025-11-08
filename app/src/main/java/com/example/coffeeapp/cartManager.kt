package com.example.coffeeapp

import android.os.Parcelable
import androidx.compose.runtime.mutableStateListOf
import kotlinx.android.parcel.Parcelize

@Parcelize
data class cartItem(
    val name: String,
    val img: String,
    val price: Double,
    val size: String,
    val quantity: Int = 1
) : Parcelable

object cartManager {
    val items = mutableStateListOf<cartItem>()

    fun addItem(item: cartItem) {
        val index = items.indexOfFirst { it.name == item.name && it.size == item.size }
        if (index != -1) {
            // Replace with updated quantity → triggers recomposition
            val existing = items[index]
            items[index] = existing.copy(quantity = existing.quantity + item.quantity)
        } else {
            items.add(item)
        }
    }

    fun updateQuantity(item: cartItem, newQuantity: Int) {
        val index = items.indexOf(item)
        if (index != -1) {
            items[index] = item.copy(quantity = newQuantity)
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
