package com.example.easy.data.api

import com.example.easy.data.model.Branch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface BranchService {
    @GET("api/branches")
    suspend fun getBranches(
        @Header("Authorization") bearerToken: String
    ): Response<List<Branch>>
}