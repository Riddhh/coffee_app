package com.example.coffeeapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardViewModel(app: Application) : AndroidViewModel(app) {
    private val store = CardStore(app.applicationContext)

    val cards = store.cardsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun addDemoCard(fullNumber: String, holder: String, expMonth: Int, expYear: Int) {
        val digits = fullNumber.filter { it.isDigit() }
        val brand = detectBrand(digits)
        val last4 = digits.takeLast(4)

        val card = SavedCard(
            id = "card_${System.currentTimeMillis()}",
            brand = brand,
            last4 = last4,
            holder = holder.trim(),
            expMonth = expMonth,
            expYear = expYear
        )

        viewModelScope.launch { store.addCard(card) }
    }

    fun removeCard(id: String) {
        viewModelScope.launch { store.removeCard(id) }
    }
}

/** same helper as before */
private fun detectBrand(d: String): String = when {
    d.startsWith("4") -> "VISA"
    d.startsWith("5") -> "MASTERCARD"
    d.startsWith("34") || d.startsWith("37") -> "AMEX"
    d.startsWith("6") -> "DISCOVER"
    else -> "CARD"
}
