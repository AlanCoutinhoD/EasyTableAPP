package com.example.easy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme // Add this import
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ViewOrdersScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Aquí verás las órdenes",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}