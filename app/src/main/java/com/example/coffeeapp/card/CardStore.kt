package com.example.coffeeapp

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "card_store")

class CardStore(private val context: Context) {
    private val KEY_CARDS = stringSetPreferencesKey("cards_set")

    val cardsFlow: Flow<List<SavedCard>> =
        context.dataStore.data.map { prefs ->
            val set = prefs[KEY_CARDS] ?: emptySet()
            set.mapNotNull(::savedCardFromLine)
                .sortedByDescending { it.id } // newest first
        }

    suspend fun addCard(card: SavedCard) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_CARDS] ?: emptySet()
            prefs[KEY_CARDS] = current + card.toLine()
        }
    }

    suspend fun removeCard(cardId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_CARDS] ?: emptySet()
            prefs[KEY_CARDS] = current.filterNot { it.startsWith("$cardId|") }.toSet()
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs -> prefs.remove(KEY_CARDS) }
    }
}
