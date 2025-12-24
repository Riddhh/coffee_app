package com.example.coffeeapp.ai

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ChatHttp {

    // ✅ Emulator → your PC Flask
    private const val BASE_URL = "http://10.0.2.2:5001/"
    // ✅ Real phone → your PC Flask (same Wi-Fi)
    // private const val BASE_URL = "http://192.168.1.11:5000/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder().build()

    val api: ChatApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(ChatApi::class.java)
}