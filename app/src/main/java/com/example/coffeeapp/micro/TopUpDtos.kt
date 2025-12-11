package com.example.coffeeapp.micro

data class TopUpRequestDto(
    val userId: String,
    val amount: Double,
    val bank: String,
    val mode: String = "TEST"
)

data class TopUpResponseDto(
    val success: Boolean,
    val transactionId: String,
    val message: String,
    val mode: String,
    val bank: String,
    val amount: Double,
    val userId: String
)