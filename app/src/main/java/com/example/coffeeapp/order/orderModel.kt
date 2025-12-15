//package com.example.coffeeapp.order
//
//data class CreateOrderRequest(
//    val userId: String,
//    val items: List<OrderItemReq>,
//    val total: Double
//)
//
//data class OrderItemReq(
//    val name: String,
//    val size: String,
//    val qty: Int,
//    val price: Double,
//    val img: String
//)
//
//data class CreateOrderResponse(
//    val success: Boolean,
//    val orderId: String
//)
package com.example.coffeeapp.order

data class OrderItemDto(
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String
)

data class CreateOrderRequest(
    val items: List<OrderItemDto>,
    val total: Double
)

data class CreateOrderResponse(
    val message: String
)

data class ServerOrder(
    val _id: String? = null,
    val userId: String? = null,
    val items: List<ServerOrderItem> = emptyList(),
    val total: Double? = null,
    val status: String? = null,
    val date: String? = null
)

data class ServerOrderItem(
    val name: String? = null,
    val price: Double? = null,
    val quantity: Int? = null,
    val imageUrl: String? = null
)