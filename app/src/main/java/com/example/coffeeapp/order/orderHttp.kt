package com.example.coffeeapp.order

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OrderHttp {
    private const val BASE_URL = "http://10.0.2.2:3002/"

    val api: OrderApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OrderApi::class.java)
    }
}
