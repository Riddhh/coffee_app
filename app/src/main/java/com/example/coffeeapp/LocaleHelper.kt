package com.example.coffeeapp

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import java.util.Locale

object LocaleHelper {
    private const val PREFS = "app_prefs"
    private const val KEY_LANG = "app_lang" // "en" or "km"

    fun setLanguage(ctx: Context, lang: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANG, lang)
            .apply()
    }

    fun getLanguage(ctx: Context): String {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANG, "en") ?: "en"
    }

    fun wrap(ctx: Context): ContextWrapper {
        val lang = getLanguage(ctx)
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val res = ctx.resources
        val config = res.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            val newCtx = ctx.createConfigurationContext(config)
            return ContextWrapper(newCtx)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            res.updateConfiguration(config, res.displayMetrics)
            return ContextWrapper(ctx)
        }
    }
}
