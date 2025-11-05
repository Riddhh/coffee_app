package com.example.coffeeapp.blockchain

// utils/ShareUtils.kt

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

fun shareJson(context: Context, fileName: String, json: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_TEXT, json)          // Fallback for most apps
        putExtra(Intent.EXTRA_SUBJECT, fileName)
    }
    context.startActivity(Intent.createChooser(sendIntent, "Export blockchain JSON"))
}

fun copyToClipboard(context: Context, label: String, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText(label, text))
}
