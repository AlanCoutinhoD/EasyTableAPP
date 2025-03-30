package com.example.easy.data.api

import com.example.easy.data.model.Order
import com.example.easy.data.model.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderService {
    @POST("api/public/orders")
    suspend fun createOrder(@Body order: Order): Response<OrderResponse>
}