package com.example.coffeeapp.blockchain


import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import io.realm.kotlin.types.EmbeddedRealmObject

// Embedded payload inside a block
class OrderEmbedded : EmbeddedRealmObject {
    var id: String = ""
    var timestamp: Long = 0L
    var itemsSummary: String = ""
    var total: Double = 0.0
}

// One row per block
class BlockRealm : RealmObject {
    @PrimaryKey
    var index: Int = 0
    var timestamp: Long = 0L
    var data: OrderEmbedded? = null
    var previousHash: String = "0"
    var nonce: Long = 0L
    var hash: String = ""
}
