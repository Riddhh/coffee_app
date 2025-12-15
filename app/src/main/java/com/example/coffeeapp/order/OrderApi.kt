package com.example.coffeeapp.order

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OrderApi {
    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") authorization: String,
        @Body req: CreateOrderRequest
    ): CreateOrderResponse

    @GET("orders")
    suspend fun getOrders(
        @Header("Authorization") authorization: String
    ): List<ServerOrder>
}
