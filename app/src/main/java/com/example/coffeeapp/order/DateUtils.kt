package com.example.coffeeapp.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun format(iso: String?): String {
        if (iso == null) return ""

        return try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            input.timeZone = TimeZone.getTimeZone("UTC")

            val date = input.parse(iso) ?: return ""
            val output = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

            output.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}
