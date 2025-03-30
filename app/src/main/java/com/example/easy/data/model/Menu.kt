package com.example.easy.data.model

data class MenuResponse(
    val category: Category?,
    val products: List<Product>
)

data class Category(
    val id: Int?,
    val name: String?
)

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val image_url: String
)