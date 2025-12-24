package com.example.coffeeapp.micro

data class AutoAcceptRequestDto(
    val userId: String,
    val transactionId: String,
    val expectedAmount: Double? = null
)

data class AutoAcceptResponseDto(
    val success: Boolean,
    val message: String,
    val transactionId: String
)
