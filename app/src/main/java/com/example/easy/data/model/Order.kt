package com.example.easy.data.model

data class Order(
    val total_amount: Double,
    val payment_method: String,
    val branch_id: Int,
    val tableNumber: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val product_id: Int,
    val quantity: Int,
    val price: Double
)

// Add response data classes
data class OrderResponse(
    val success: Boolean,
    val message: String,
    val data: OrderResponseData?
)

data class OrderResponseData(
    val id: Int,
    val total_amount: Double,
    val payment_method: String,
    val branch_id: Int,
    val tableNumber: String,
    val status: String,
    val created_at: String,
    val items: List<OrderItemResponse>
)

data class OrderItemResponse(
    val id: Int,
    val product_id: Int,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)