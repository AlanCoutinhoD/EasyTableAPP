package com.example.easy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.easy.ui.theme.PrimaryBlack
import com.example.easy.ui.theme.TextPrimary
import com.example.easy.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onCreateOrderClick: () -> Unit,
    onViewOrdersClick: () -> Unit
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
            text = "¡Bienvenido!",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "¿Qué deseas hacer hoy?",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onCreateOrderClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlack
            )
        ) {
            Text("Crear Orden")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onViewOrdersClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlack
            )
        ) {
            Text("Ver Órdenes")
        }
    }
}