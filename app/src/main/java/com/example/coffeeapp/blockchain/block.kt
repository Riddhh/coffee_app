package com.example.coffeeapp.blockchain


import java.security.MessageDigest

data class Block(
    val index: Int,
    val timestamp: Long,
    val dataJson: String,   // JSON of Order (or any payload)
    val previousHash: String,
    var nonce: Long = 0L,
    var hash: String = ""
) {
    fun calculateHash(): String {
        val input = "$index$timestamp$dataJson$previousHash$nonce"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun mine(difficulty: Int) {
        val prefix = "0".repeat(difficulty)
        do {
            nonce++
            hash = calculateHash()
        } while (!hash.startsWith(prefix))
    }
}
