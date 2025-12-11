package com.example.coffeeapp.micro

import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {

    @POST("/api/topup/test")
    suspend fun topUpTest(
        @Body request: TopUpRequestDto
    ): TopUpResponseDto
}