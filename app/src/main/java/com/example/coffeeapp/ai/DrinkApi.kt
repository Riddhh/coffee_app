package com.example.coffeeapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface DrinkService {
    @GET("random_drink")
    suspend fun getRandomDrink(): DrinkResponse
}

object DrinkApi {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // ⚠️ If you're on an emulator, use http://10.0.2.2:5000/
//    private const val BASE_URL = "http://127.0.0.1:5001/"
    private const val BASE_URL = "http://10.0.2.2:5001/"


    val service: DrinkService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DrinkService::class.java)
}