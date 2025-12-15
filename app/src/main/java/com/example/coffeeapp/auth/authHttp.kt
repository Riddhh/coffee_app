package com.example.coffeeapp.auth


import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Http {
    fun api(ctx: Context): AuthApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(ctx))
            .build()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3001/") // Emulator→your laptop localhost. On device, use your LAN IP.
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}
