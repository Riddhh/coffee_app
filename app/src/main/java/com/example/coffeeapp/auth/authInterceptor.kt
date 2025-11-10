package com.example.coffeeapp.auth


import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val ctx: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenStore.get(ctx)
        val req = if (token != null)
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        else chain.request()
        return chain.proceed(req)
    }
}
