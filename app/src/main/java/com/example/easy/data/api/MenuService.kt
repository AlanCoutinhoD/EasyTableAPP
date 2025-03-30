package com.example.easy.data.api

import com.example.easy.data.model.MenuResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MenuService {
    @GET("api/public/menu/business/{businessId}")
    suspend fun getBusinessMenu(
        @Path("businessId") businessId: Int,
        @Header("Authorization") token: String
    ): Response<List<MenuResponse>>
}