package com.example.coffeeapp.ai

import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("chat")
    suspend fun chat(@Body req: ChatRequest): ChatResponse
}