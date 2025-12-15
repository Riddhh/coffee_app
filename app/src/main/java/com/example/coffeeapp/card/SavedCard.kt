package com.example.coffeeapp

data class SavedCard(
    val id: String,
    val brand: String,
    val last4: String,
    val holder: String,
    val expMonth: Int,
    val expYear: Int
)

/** Demo encode/decode (simple, no extra libs) */
fun SavedCard.toLine(): String =
    listOf(id, brand, last4, holder.replace("|", " "), expMonth.toString(), expYear.toString())
        .joinToString("|")

fun savedCardFromLine(line: String): SavedCard? {
    val p = line.split("|")
    if (p.size < 6) return null
    return SavedCard(
        id = p[0],
        brand = p[1],
        last4 = p[2],
        holder = p[3],
        expMonth = p[4].toIntOrNull() ?: return null,
        expYear = p[5].toIntOrNull() ?: return null
    )
}
