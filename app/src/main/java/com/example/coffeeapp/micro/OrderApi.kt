package com.example.coffeeapp.micro

import com.example.coffeeapp.order.CreateOrderRequest
import com.example.coffeeapp.order.CreateOrderResponse
import com.example.coffeeapp.order.ServerOrder
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class OrderItem(
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null
)

data class CreateOrderReq(
    val items: List<OrderItem>,
    val total: Double
)

data class CreateOrderRes(
    val message: String,
    val order: Any? = null
)

interface OrderApi {
    @POST("orders")
    suspend fun createOrder(
        @Body req: CreateOrderRequest
    ): CreateOrderResponse

    @GET("orders")
    suspend fun getOrders(): List<ServerOrder>
}
