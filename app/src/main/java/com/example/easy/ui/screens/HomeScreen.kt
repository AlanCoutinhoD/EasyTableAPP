package com.example.easy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.easy.ui.theme.PrimaryBlack
import com.example.easy.ui.theme.TextPrimary
import com.example.easy.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onCreateOrder: () -> Unit,
    onViewOrders: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = TextPrimary
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "¿Qué deseas hacer hoy?",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onCreateOrder,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlack
            )
        ) {
            Text("Realizar Orden")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onViewOrders,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlack
            )
        ) {
            Text("Ver Órdenes")
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Cerrar Sesión",
                color = TextSecondary
            )
        }
    }
}