package com.example.easy.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.56.1:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val orderService: OrderService = retrofit.create(OrderService::class.java)
}