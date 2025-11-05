package com.example.coffeeapp.blockchain


import android.R.attr.order
import com.example.coffeeapp.model.Order
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.util.UUID

object RealmBlockchainRepository {
    private const val DIFFICULTY = 3 // tweak for speed vs PoW demo

    private val config by lazy {
        RealmConfiguration.Builder(
            schema = setOf(BlockRealm::class, OrderEmbedded::class, BalanceRealm::class)
        ).name("coffee_blockchain.realm").build()
    }
    private val realm: Realm by lazy { Realm.open(config) }

    /** Reactive stream of blocks sorted by index */
    val blocksFlow: Flow<List<BlockRealm>> =
        realm.query(BlockRealm::class).sort("index", Sort.ASCENDING).asFlow().map { it.list }

    /** Call once on startup */
    suspend fun ensureGenesis() {
        val first = realm.query(BlockRealm::class, "index == 0").first().find()
        if (first == null) {
            realm.write {
                val genesis = BlockRealm().apply {
                    index = 0
                    timestamp = System.currentTimeMillis()
                    previousHash = "0"
                    nonce = 0L
                }
                mineBlock(genesis, DIFFICULTY)
                copyToRealm(genesis)
            }
        }
    }

    /** Add a mined block for the given order (run off main thread) */
    suspend fun addOrder(order: Order): BlockRealm {
        val last = realm.query(BlockRealm::class).sort("index", Sort.DESCENDING).first().find()
            ?: throw IllegalStateException("Call ensureGenesis() first")

        val newBlock = BlockRealm().apply {
            index = last.index + 1
            timestamp = System.currentTimeMillis()
            data = OrderEmbedded().also {
                it.id = order.id
                it.timestamp = order.timestamp
                it.itemsSummary = order.itemsSummary
                it.total = order.total
            }
            previousHash = last.hash
            nonce = 0L
        }
        mineBlock(newBlock, DIFFICULTY)
        realm.write { copyToRealm(newBlock) }
        return newBlock
    }

    /** Verify full chain integrity */
    fun isValid(): Boolean {
        val blocks = realm.query(BlockRealm::class).sort("index", Sort.ASCENDING).find()
        if (blocks.isEmpty()) return false
        for (i in 1 until blocks.size) {
            val prev = blocks[i - 1]
            val curr = blocks[i]
            val expected = calculateHash(curr)
            if (curr.hash != expected) return false
            if (curr.previousHash != prev.hash) return false
            if (!curr.hash.startsWith("0".repeat(DIFFICULTY))) return false
        }
        return true
    }

    // -------- Helpers --------
    private fun calculateHash(b: BlockRealm): String {
        val payload = b.data
        val dataStr = listOf(
            payload?.id.orEmpty(),
            payload?.timestamp?.toString().orEmpty(),
            payload?.itemsSummary.orEmpty(),
            payload?.total?.toString().orEmpty()
        ).joinToString("|")

        val input = "${b.index}${b.timestamp}$dataStr${b.previousHash}${b.nonce}"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun mineBlock(b: BlockRealm, difficulty: Int) {
        val target = "0".repeat(difficulty)
        do {
            b.nonce++
            b.hash = calculateHash(b)
        } while (!b.hash.startsWith(target))
    }

    suspend fun getBalance(): Double {
        val result = realm.query(BalanceRealm::class).first().find()
        return result?.balance ?: 0.0
    }

    suspend fun updateBalance(amount: Double) {
        realm.write {
            val balanceObj = query(BalanceRealm::class, "id == 1").first().find()
                ?: BalanceRealm().apply { id = 1 }

            balanceObj.balance += amount
            copyToRealm(balanceObj, updatePolicy = io.realm.kotlin.UpdatePolicy.ALL)
        }
    }

    suspend fun addTopUpBlock(amount: Double) {
        val order = Order(
            id = "TOPUP-${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            itemsSummary = "Top-Up",
            total = amount
        )

        addOrder(order) // ✅ Store in blockchain (Realm)
        updateBalance(amount) // ✅ Increase balance
    }

}
