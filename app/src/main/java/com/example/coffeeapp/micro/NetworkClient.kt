package com.example.coffeeapp.micro

import com.example.coffeeapp.auth.AuthApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {

    private const val BASE_PRODUCTS = "http://10.0.2.2:3000/"
    private const val BASE_AUTH     = "http://10.0.2.2:3001/"
    private const val BASE_ORDERS   = "http://10.0.2.2:3002/"
    private const val BASE_PAYMENT  = "http://10.0.2.2:8000/"

    private fun retrofit(baseUrl: String, client: OkHttpClient? = null): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .apply { if (client != null) client(client) }
            .build()

    // 🔐 Auth (login/register)
    val authApi: AuthApi =
        retrofit(BASE_AUTH).create(AuthApi::class.java)

    // ☕ Products
    val productApi: ProductApi =
        retrofit(BASE_PRODUCTS).create(ProductApi::class.java)

    // 💳 Payment (blockchain / FastAPI)
    val paymentApi: PaymentApiService =
        retrofit(BASE_PAYMENT).create(PaymentApiService::class.java)

    // 📦 Orders (JWT protected)
    fun orderApi(tokenProvider: () -> String?): OrderApi {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = tokenProvider()
                val req = if (!token.isNullOrBlank()) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else chain.request()
                chain.proceed(req)
            }
            .build()

        return retrofit(BASE_ORDERS, client).create(OrderApi::class.java)
    }
}
