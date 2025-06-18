package com.akuntansimandiri.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Int,
    val price: Int,
    val place: String,
    val date: String,
    val userId: Int // tambahkan ini
) {
    val totalPrice: Int
        get() = quantity * price
}

