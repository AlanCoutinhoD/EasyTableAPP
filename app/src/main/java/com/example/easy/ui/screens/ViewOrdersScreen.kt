package com.example.easy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.easy.network.NetworkModule
import com.example.easy.network.OrderResponse
import kotlinx.coroutines.launch

@Composable
fun ViewOrdersScreen(branchId: Int, token: String) {
    val orderService = NetworkModule.orderService
    val coroutineScope = rememberCoroutineScope()
    var orders by remember { mutableStateOf<List<OrderResponse>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                orders = orderService.getPreparedOrders(branchId, "Bearer $token")
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (orders != null) {
            Column {
                orders!!.forEach { order ->
                    Text("Order ID: ${order.id}, Total: ${order.total_amount}")
                    order.items.forEach { item ->
                        Text("Item: ${item.product_name}, Quantity: ${item.quantity}, Price: ${item.price}")
                    }
                }
            }
        } else if (errorMessage != null) {
            Text("Error: $errorMessage")
        } else {
            CircularProgressIndicator()
        }
    }
}