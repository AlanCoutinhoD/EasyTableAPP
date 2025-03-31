package com.example.easy.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

data class OrderItem(
    val id: Int,
    val product_name: String,
    val description: String,
    val quantity: Int,
    val price: String,
    val total: Double
)

data class OrderResponse(
    val id: Int,
    val user_id: Int?,
    val branch_id: Int,
    val tableNumber: String,
    val total_amount: String,
    val status: String,
    val payment_status: String,
    val payment_method: String,
    val delivery_address: String?,
    val notes: String?,
    val created_at: String,
    val updated_at: String,
    val items: List<OrderItem>
)

interface OrderService {
    @GET("api/public/orders/branch/{branchid}/prepared")
    suspend fun getPreparedOrders(
        @Path("branchid") branchId: Int,
        @Header("Authorization") token: String
    ): List<OrderResponse>
}