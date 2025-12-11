package com.example.coffeeapp.micro

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {

    // For Android emulator talking to your laptop's localhost
    private const val BASE_URL = "http://10.0.2.2:8000"

    val paymentApi: PaymentApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaymentApiService::class.java)
    }
}