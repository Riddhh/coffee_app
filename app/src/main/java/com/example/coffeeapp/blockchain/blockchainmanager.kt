package com.example.coffeeapp.blockchain

import com.example.coffeeapp.model.Order
import com.google.gson.Gson

object BlockchainManager {
    private val gson = Gson()
    private val _chain = mutableListOf<Block>()
    val chain: List<Block> get() = _chain

    // Keep this small so mining is fast on device
    private const val DIFFICULTY = 3

    init {
        if (_chain.isEmpty()) {
            val genesis = Block(
                index = 0,
                timestamp = System.currentTimeMillis(),
                dataJson = gson.toJson(mapOf("genesis" to true)),
                previousHash = "0"
            )
            genesis.mine(DIFFICULTY)
            _chain += genesis
        }
    }

    /** Add an order as a new block */
    fun addOrder(order: Order): Block {
        val last = _chain.last()
        val block = Block(
            index = last.index + 1,
            timestamp = System.currentTimeMillis(),
            dataJson = gson.toJson(order),
            previousHash = last.hash
        )
        block.mine(DIFFICULTY)
        _chain += block
        return block
    }

    /** Full chain validation */
    fun isValid(): Boolean {
        for (i in 1 until _chain.size) {
            val prev = _chain[i - 1]
            val curr = _chain[i]
            val expectedHash = curr.calculateHash()
            if (curr.hash != expectedHash) return false
            if (curr.previousHash != prev.hash) return false
            if (!curr.hash.startsWith("0".repeat(DIFFICULTY))) return false
        }
        return true
    }
}
