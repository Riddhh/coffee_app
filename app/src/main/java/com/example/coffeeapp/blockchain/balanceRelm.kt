package com.example.coffeeapp.blockchain



import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class BalanceRealm : RealmObject {
    @PrimaryKey
    var id: Int = 1 // Always only one balance entry
    var balance: Double = 0.0
}
