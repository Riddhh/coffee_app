package com.example.coffeeapp.micro

import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {

    @POST("/api/topup/test")
    suspend fun topUpTest(
        @Body request: TopUpRequestDto
    ): TopUpResponseDto

    @POST("/api/topup/verify")
    suspend fun verifyTopUp(
        @Body request: VerifyReceiptRequestDto
    ): VerifyReceiptResponseDto

    @POST("/api/topup/consume")
    suspend fun consumeTopUp(
        @Body request: ConsumeReceiptRequestDto
    ): ConsumeReceiptResponseDto
}
