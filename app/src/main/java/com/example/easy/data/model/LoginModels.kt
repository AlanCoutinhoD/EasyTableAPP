package com.example.easy.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)

data class LoginResponse(
    val token: String,
    val user: User
)