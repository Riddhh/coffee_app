package com.example.coffeeapp.micro

data class TopUpRequestDto(
    val userId: String,
    val amount: Double,
    val bank: String,
    val mode: String = "TEST"
)

// ✅ add chainReceiptTxHash (matches FastAPI)
data class TopUpResponseDto(
    val success: Boolean,
    val transactionId: String,
    val message: String,
    val mode: String,
    val bank: String,
    val amount: Double,
    val userId: String,
    val chainReceiptTxHash: String? = null
)

data class VerifyReceiptRequestDto(
    val userId: String,
    val transactionId: String,
    val expectedAmount: Double? = null
)

data class VerifyReceiptResponseDto(
    val valid: Boolean,
    val reason: String,
    val transactionId: String,
    val amount: Double? = null,
    val consumed: Boolean? = null,
    val createdAt: Long? = null
)

data class ConsumeReceiptRequestDto(
    val transactionId: String
)

data class ConsumeReceiptResponseDto(
    val success: Boolean,
    val transactionId: String,
    val chainConsumeTxHash: String? = null,
    val message: String
)
