package com.example.coffeeapp.auth


import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object TokenStore {
    private const val FILE = "auth_store"
    private const val KEY = "jwt"

    private fun prefs(ctx: Context) = EncryptedSharedPreferences.create(
        FILE,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        ctx,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun save(ctx: Context, token: String?) = prefs(ctx).edit().putString(KEY, token).apply()
    fun get(ctx: Context): String? = prefs(ctx).getString(KEY, null)
}
